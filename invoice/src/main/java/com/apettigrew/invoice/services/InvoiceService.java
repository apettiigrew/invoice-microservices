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
import java.util.ArrayList;
import java.util.List;

@Service
@Transactional
@AllArgsConstructor
public class InvoiceService {
    private static final Logger log = LoggerFactory.getLogger(InvoiceService.class);

    @Autowired
    private InvoiceRepository invoiceRepository;

    private final StreamBridge streamBridge;

    @Autowired
    @Qualifier("skipNullModelMapper")
    private ModelMapper modelMapper;

    public Page<Invoice> getAllInvoices(String userId, Pageable pageable, InvoiceStatus status) {
        if(status != null){
            return invoiceRepository.findByUserIdAndStatus(userId, status, pageable);
        }

        return invoiceRepository.findAllActiveByUserId(userId, pageable);
    }

    public Invoice getInvoiceById(Integer id, String userId) {
        return invoiceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice ID "+id+" not found"));
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
        // Note: We explicitly calculate and set the total, ignoring any value that may have been
        // passed in the DTO (which should be null anyway due to @JsonProperty(access = READ_ONLY))
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

    private void sendCommunication(Invoice invoice) {
        var invoiceDto = modelMapper.map(invoice,InvoiceDto.class);

        log.info("Sending Communication request for the details: {}", invoiceDto);
        var result = streamBridge.send("sendCommunication-out-0", invoiceDto);
        log.info("Is the Communication request successfully triggered ? : {}", result);
    }

    public Invoice updateInvoice(Integer id, com.apettigrew.invoice.dtos.InvoiceDto invoiceDto, String userId) {
        // Validate that invoice items are present
        if (invoiceDto.getInvoiceItems() == null || invoiceDto.getInvoiceItems().isEmpty()) {
            throw new IllegalArgumentException("Invoice must have at least 1 invoice item");
        }

        Invoice existingInvoice = invoiceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice ID "+id+" not found"));
        ObjectMapper objectMapper = new ObjectMapper();
        int existingId = existingInvoice.getId();
        modelMapper.map(invoiceDto,existingInvoice);
        existingInvoice.setId(existingId);
        existingInvoice.setUserId(userId); // Ensure userId is not overwritten
        
        // Calculate total from invoice items (qty * price for each, then sum)
        // Note: We explicitly calculate and set the total, ignoring any value that may have been
        // passed in the DTO (which should be null anyway due to @JsonProperty(access = READ_ONLY))
        BigDecimal invoiceTotal = calculateInvoiceTotal(invoiceDto.getInvoiceItems());
        existingInvoice.setTotal(invoiceTotal);
        
        // Update invoice items - clear existing and create new ones
        if (existingInvoice.getInvoiceItems() != null) {
            existingInvoice.getInvoiceItems().clear();
        }
        List<InvoiceItem> invoiceItems = createInvoiceItems(invoiceDto.getInvoiceItems(), existingInvoice);
        existingInvoice.setInvoiceItems(invoiceItems);
        
        Invoice updatedInvoice;

        try {
            existingInvoice.setSenderAddress(objectMapper.writeValueAsString(invoiceDto.getSenderAddress()));
            existingInvoice.setClientAddress(objectMapper.writeValueAsString(invoiceDto.getClientAddress()));
            updatedInvoice = invoiceRepository.save(existingInvoice);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

       return updatedInvoice;
    }

    public void deleteInvoice(Integer id, String userId) {
        Invoice invoice = invoiceRepository.findByIdAndUserId(id, userId)
                .orElseThrow(() -> new InvoiceNotFoundException("Invoice ID "+id+" not found"));
        invoice.setDeletedAt(LocalDateTime.now());
        invoiceRepository.save(invoice);
    }
}
