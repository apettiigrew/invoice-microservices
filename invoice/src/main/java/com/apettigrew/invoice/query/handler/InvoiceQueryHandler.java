package com.apettigrew.invoice.query.handler;

import com.apettigrew.invoice.dtos.InvoiceDto;
import com.apettigrew.invoice.entities.Invoice;
import com.apettigrew.invoice.query.FindInvoiceQuery;
import com.apettigrew.invoice.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class InvoiceQueryHandler {

    private final InvoiceService invoiceService;

    @QueryHandler
    public Invoice findCustomer(FindInvoiceQuery findInvoiceQuery) {
        return invoiceService.getInvoiceById(findInvoiceQuery.getId());
    }
}
