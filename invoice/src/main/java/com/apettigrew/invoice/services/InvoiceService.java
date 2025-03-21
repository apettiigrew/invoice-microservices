package com.apettigrew.invoice.services;

import com.apettigrew.invoice.dtos.InvoiceDto;
import com.apettigrew.invoice.entities.Invoice;
import com.apettigrew.invoice.enums.InvoiceStatus;
import com.apettigrew.invoice.exceptions.InvoiceNotFoundException;
import com.apettigrew.invoice.respositories.InvoiceRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class InvoiceService {
    @Autowired
    private InvoiceRepository invoiceRepository;

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

        return savedInvoice;
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
