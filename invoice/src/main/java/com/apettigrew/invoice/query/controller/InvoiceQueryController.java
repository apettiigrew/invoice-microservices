package com.apettigrew.invoice.query.controller;

import com.apettigrew.invoice.dtos.InvoiceDto;
import com.apettigrew.invoice.entities.Invoice;
import com.apettigrew.invoice.jsonapi.InvoiceResource;
import com.apettigrew.invoice.jsonapi.SingleResourceResponse;
import com.apettigrew.invoice.query.FindInvoiceQuery;
import lombok.RequiredArgsConstructor;
import org.axonframework.messaging.responsetypes.ResponseTypes;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api", produces = {MediaType.APPLICATION_JSON_VALUE})
@Validated
@RequiredArgsConstructor
public class InvoiceQueryController {
    private final QueryGateway queryGateway;

    @GetMapping("/{id}")
    public SingleResourceResponse<InvoiceResource> getInvoiceById(final @PathVariable("id") String id) {
        FindInvoiceQuery findInvoiceQuery = new FindInvoiceQuery(id);
        Invoice invoice = queryGateway.query(findInvoiceQuery, ResponseTypes.instanceOf(Invoice.class)).join();

        return new SingleResourceResponse<>(InvoiceResource.toResource(invoice));
    }
}
