package com.apettigrew.invoice.query.projection;

import com.apettigrew.invoice.command.event.InvoiceCreatedEvent;
import com.apettigrew.invoice.command.event.InvoiceUpdatedEvent;
import com.apettigrew.invoice.dtos.InvoiceDto;
import com.apettigrew.invoice.entities.Invoice;
import com.apettigrew.invoice.services.InvoiceService;
import lombok.RequiredArgsConstructor;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@ProcessingGroup("invoice-group")
public class InvoiceProjection {

    private final InvoiceService invoiceService;

    @EventHandler
    public void on(InvoiceCreatedEvent invoiceCreatedEvent) {
        var invoiceDto = new InvoiceDto();
        BeanUtils.copyProperties(invoiceCreatedEvent,invoiceDto);
        invoiceService.createInvoice(invoiceDto);
    }

    @EventHandler
    public void on(InvoiceUpdatedEvent invoiceUpdatedEvent) {
        // throw new RuntimeException("It is a bad day!!");
        var invoiceDto = new InvoiceDto();
        BeanUtils.copyProperties(invoiceUpdatedEvent,invoiceDto);
        invoiceService.updateInvoice(invoiceDto.getId(), invoiceDto);
    }

}
