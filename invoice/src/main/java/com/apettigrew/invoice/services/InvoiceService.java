package com.apettigrew.invoice.services;

import com.apettigrew.invoice.dtos.InvoiceDto;
import com.apettigrew.invoice.entities.Invoice;
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

    public Page<Invoice> getAllInvoices(Pageable pageable, InvoiceStatus status) {
        if(status != null){
            return invoiceRepository.findByStatus(status,pageable);
        }

        return invoiceRepository.findAll(pageable);
    }

    public Invoice getInvoiceById(String id) {
        return invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException("Invoice ID "+id+" not found"));
    }

    public Invoice createInvoice(InvoiceDto invoiceDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        Invoice invoice = modelMapper.map(invoiceDto, Invoice.class);
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

    private void sendCommunication(Invoice invoice) {
        var invoiceDto = modelMapper.map(invoice,InvoiceDto.class);

        log.info("Sending Communication request for the details: {}", invoiceDto);
        var result = streamBridge.send("sendCommunication-out-0", invoiceDto);
        log.info("Is the Communication request successfully triggered ? : {}", result);
    }

    public Invoice updateInvoice(String id, com.apettigrew.invoice.dtos.InvoiceDto invoiceDto) {
        Invoice existingInvoice = invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
        ObjectMapper objectMapper = new ObjectMapper();
        int existingId = existingInvoice.getId();
        modelMapper.map(invoiceDto,existingInvoice);
        existingInvoice.setId(existingId);
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

    public void deleteInvoice(String id) {
        invoiceRepository.deleteById(id);
    }
}
