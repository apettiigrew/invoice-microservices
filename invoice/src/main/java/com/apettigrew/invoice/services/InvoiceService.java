package com.apettigrew.invoice.services;

import com.apettigrew.invoice.dtos.InvoiceDto;
import com.apettigrew.invoice.dtos.InvoiceItemDto;
import com.apettigrew.invoice.entities.Invoice;
import com.apettigrew.invoice.entities.InvoiceItem;
import com.apettigrew.invoice.enums.InvoiceStatus;
import com.apettigrew.invoice.exceptions.InvoiceNotFoundException;
import com.apettigrew.invoice.respositories.InvoiceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.cloud.stream.function.StreamBridge;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class InvoiceService {
    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);
    private final StreamBridge streamBridge;
    @Autowired
    private InvoiceRepository invoiceRepository;
    @Autowired
    @Qualifier("skipNullModelMapper")
    private ModelMapper modelMapper;

    public Page<Invoice> getAllInvoices(String userId, Pageable pageable, InvoiceStatus status) {
        if (status != null) {
            return invoiceRepository.findByUserIdAndStatus(userId, status, pageable);
        }

        return invoiceRepository.findAllActiveByUserId(userId, pageable);
    }

    public Invoice getInvoiceById(Integer id, String userId) {
        return invoiceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice ID " + id + " not found"));
    }

    public Invoice createInvoice(InvoiceDto invoiceDto, String userId) {
        // Validate that invoice items are present
        if (invoiceDto.getInvoiceItems() == null || invoiceDto.getInvoiceItems().isEmpty()) {
            throw new IllegalArgumentException("Invoice must have at least 1 invoice item");
        }

        ObjectMapper objectMapper = new ObjectMapper();
        Invoice invoice = modelMapper.map(invoiceDto, Invoice.class);
        invoice.setUserId(userId);

        // Calculate total from invoice items (qty * price for each, then sum)
        // Note: We explicitly calculate and set the total, ignoring any value that may
        // have been
        // passed in the DTO (which should be null anyway due to @JsonProperty(access =
        // READ_ONLY))
        BigDecimal invoiceTotal = calculateInvoiceTotal(invoiceDto.getInvoiceItems());
        invoice.setTotal(invoiceTotal);

        // Create and set invoice items
        List<InvoiceItem> invoiceItems = createInvoiceItems(invoiceDto.getInvoiceItems(), invoice);
        invoice.setInvoiceItems(invoiceItems);

        Invoice savedInvoice;

        try {
            invoice.setSenderAddress(objectMapper.writeValueAsString(invoiceDto.getSenderAddress()));
            invoice.setClientAddress(objectMapper.writeValueAsString(invoiceDto.getClientAddress()));
            savedInvoice = invoiceRepository.save(invoice);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        sendCommunication(savedInvoice);

        return savedInvoice;
    }

    private BigDecimal calculateInvoiceTotal(List<InvoiceItemDto> invoiceItemDtos) {
        return invoiceItemDtos.stream()
                .map(item -> item.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    private List<InvoiceItem> createInvoiceItems(List<InvoiceItemDto> invoiceItemDtos, Invoice invoice) {
        List<InvoiceItem> invoiceItems = new ArrayList<>();
        for (InvoiceItemDto itemDto : invoiceItemDtos) {
            InvoiceItem item = modelMapper.map(itemDto, InvoiceItem.class);
            item.setInvoice(invoice);
            // Calculate total for each item (qty * price)
            BigDecimal itemTotal = itemDto.getPrice().multiply(BigDecimal.valueOf(itemDto.getQuantity()));
            item.setTotal(itemTotal);
            invoiceItems.add(item);
        }
        return invoiceItems;
    }

    /**
     * Updates invoice items intelligently based on IDs:
     * - Items with IDs: updates existing items
     * - Items without IDs (null or 0): creates new items
     * - Existing items not in the request: removes them
     */
    private List<InvoiceItem> updateInvoiceItems(
            List<InvoiceItem> existingInvoiceItems,
            List<InvoiceItemDto> invoiceItemsToUpdateDtos,
            Invoice existingInvoice) {

        List<InvoiceItem> updatedItems = new ArrayList<>();

        Map<Integer, InvoiceItem> existingInvoiceItemMap = new HashMap<>();
        for (InvoiceItem item : existingInvoiceItems) {
            existingInvoiceItemMap.put(item.getId(), item);
        }
        // Collect IDs from the request to identify which items should be kept
        Set<Integer> existingItemIds = existingInvoiceItems.stream()
                .map(InvoiceItem::getId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        // Collect IDs from the request to identify which items should be kept
        Set<Integer> requestedItemIds = invoiceItemsToUpdateDtos.stream()
                .map(InvoiceItemDto::getId)
                .filter(id -> id != null && id > 0)
                .collect(Collectors.toSet());

        // Soft delete items that exist but are not in the request
        for (InvoiceItem existingItem : existingInvoiceItems) {
            if (!requestedItemIds.contains(existingItem.getId())) {
                existingItem.setDeletedAt(LocalDateTime.now());
            }
        }

        // Process each item in the request
        for (InvoiceItemDto itemToUpdateDto : invoiceItemsToUpdateDtos) {
            Integer itemId = itemToUpdateDto.getId();

            if (itemId != null && itemId > 0 && existingItemIds.contains(itemId)) {
                // Update existing item
                InvoiceItem existingItem = existingInvoiceItemMap.get(itemId);
                existingItem.setName(itemToUpdateDto.getName());
                existingItem.setQuantity(itemToUpdateDto.getQuantity());
                existingItem.setPrice(itemToUpdateDto.getPrice());
                // Recalculate total
                BigDecimal itemTotal = itemToUpdateDto.getPrice()
                        .multiply(BigDecimal.valueOf(itemToUpdateDto.getQuantity()));
                existingItem.setTotal(itemTotal);
                updatedItems.add(existingItem);
            } else {

                InvoiceItem newItem = new InvoiceItem();
                newItem.setName(itemToUpdateDto.getName());
                newItem.setQuantity(itemToUpdateDto.getQuantity());
                newItem.setPrice(itemToUpdateDto.getPrice());

                BigDecimal itemTotal = itemToUpdateDto.getPrice()
                        .multiply(BigDecimal.valueOf(itemToUpdateDto.getQuantity()));
                newItem.setTotal(itemTotal);
                updatedItems.add(newItem);
            }
        }

        return updatedItems;
    }

    private void sendCommunication(Invoice invoice) {
        var invoiceDto = modelMapper.map(invoice, InvoiceDto.class);

        log.info("Sending Communication request for the details: {}", invoiceDto);
        var result = streamBridge.send("sendCommunication-out-0", invoiceDto);
        log.info("Is the Communication request successfully triggered ? : {}", result);
    }

    public Invoice updateInvoice(Integer invoiceId, InvoiceDto invoiceDto, String userId) {
        try {
            // Validate that invoice items are present
            if (invoiceDto.getInvoiceItems() == null || invoiceDto.getInvoiceItems().isEmpty()) {
                throw new IllegalArgumentException("Invoice must have at least 1 invoice item");
            }

            Invoice existingInvoice = invoiceRepository.findByIdAndUserId(invoiceId, userId)
                    .orElseThrow(() -> new InvoiceNotFoundException("Invoice ID " + invoiceId + " not found"));

            // Update invoice properties
            existingInvoice.setPaymentDue(invoiceDto.getPaymentDue());
            existingInvoice.setDescription(invoiceDto.getDescription());
            existingInvoice.setPaymentTerms(invoiceDto.getPaymentTerms());
            existingInvoice.setClientName(invoiceDto.getClientName());
            existingInvoice.setClientEmail(invoiceDto.getClientEmail());
            existingInvoice.setStatus(invoiceDto.getStatus());
            ObjectMapper objectMapper = new ObjectMapper();
            existingInvoice.setSenderAddress(objectMapper.writeValueAsString(invoiceDto.getSenderAddress()));
            existingInvoice.setClientAddress(objectMapper.writeValueAsString(invoiceDto.getClientAddress()));

            // Update invoice items intelligently based on IDs
            List<InvoiceItem> updatedInvoiceItems = updateInvoiceItems(
                    existingInvoice.getInvoiceItems(),
                    invoiceDto.getInvoiceItems(),
                    existingInvoice);

            existingInvoice.setInvoiceItems(updatedInvoiceItems);

            // calculate the new total invoices based on any updated invoice items
            List<InvoiceItemDto> updatedInvoiceItemDtos = updatedInvoiceItems.stream()
                    .map((item) -> modelMapper.map(item, InvoiceItemDto.class)).collect(Collectors.toList());

            BigDecimal invoiceTotal = calculateInvoiceTotal(updatedInvoiceItemDtos);
            existingInvoice.setTotal(invoiceTotal);

            return invoiceRepository.save(existingInvoice);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

    public void deleteInvoice(Integer id, String userId) {
        Invoice invoice = invoiceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice ID " + id + " not found"));
        invoice.setDeletedAt(LocalDateTime.now());
        invoiceRepository.save(invoice);
    }
}
