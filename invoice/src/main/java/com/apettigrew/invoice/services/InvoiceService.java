package com.apettigrew.invoice.services;

import com.apettigrew.invoice.entities.Invoice;
import com.apettigrew.invoice.exceptions.InvoiceNotFoundException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class InvoiceService {
    @Autowired
    private com.apettigrew.invoice.respositories.InvoiceRepository invoiceRepository;

    @Autowired
    @Qualifier("skipNullModelMapper")
    private ModelMapper modelMapper;


    public Page<com.apettigrew.invoice.entities.Invoice> getAllInvoices(Pageable pageable, com.apettigrew.invoice.enums.InvoiceStatus status) {
        if(status != null){
            return invoiceRepository.findByStatus(status,pageable);
        }

        return invoiceRepository.findAll(pageable);
    }

    public com.apettigrew.invoice.entities.Invoice getInvoiceById(Integer id) {
        return invoiceRepository.findById(id).orElseThrow(() -> new InvoiceNotFoundException("Invoice ID "+id+" not found"));
    }

    public com.apettigrew.invoice.entities.Invoice createInvoice(com.apettigrew.invoice.dtos.InvoiceDto invoiceDto) {
        ObjectMapper objectMapper = new ObjectMapper();
        Invoice invoice = modelMapper.map(invoiceDto, com.apettigrew.invoice.entities.Invoice.class);
        try {
            invoice.setSenderAddress(objectMapper.writeValueAsString(invoiceDto.getSenderAddress()));
            invoice.setClientAddress(objectMapper.writeValueAsString(invoiceDto.getClientAddress()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

        return invoiceRepository.save(invoice);
    }

    public com.apettigrew.invoice.entities.Invoice updateInvoice(Integer id, com.apettigrew.invoice.dtos.InvoiceDto invoiceDto) {
        com.apettigrew.invoice.entities.Invoice existingInvoice = invoiceRepository.findById(id).orElseThrow(() -> new RuntimeException("Invoice not found"));
        ObjectMapper objectMapper = new ObjectMapper();
        modelMapper.map(invoiceDto,existingInvoice);
        try {
            existingInvoice.setSenderAddress(objectMapper.writeValueAsString(invoiceDto.getSenderAddress()));
            existingInvoice.setClientAddress(objectMapper.writeValueAsString(invoiceDto.getClientAddress()));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }

       return invoiceRepository.save(existingInvoice);
    }

    public void deleteInvoice(Integer id) {
        invoiceRepository.deleteById(id);
    }
}
