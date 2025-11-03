package com.apettigrew.invoice.controllers;


import com.apettigrew.invoice.dtos.ContactInfoDto;
import com.apettigrew.invoice.dtos.InvoiceDto;
import com.apettigrew.invoice.entities.Invoice;
import com.apettigrew.invoice.enums.InvoiceStatus;
import com.apettigrew.invoice.jsonapi.InvoiceResource;
import com.apettigrew.invoice.jsonapi.JsonApiConstants;
import com.apettigrew.invoice.jsonapi.MultipleResourceResponse;
import com.apettigrew.invoice.jsonapi.SingleResourceResponse;
import com.apettigrew.invoice.jsonapi.requests.CreateRequest;
import com.apettigrew.invoice.jsonapi.requests.InvoiceCreateRequest;
import com.apettigrew.invoice.jsonapi.requests.UpdateRequest;
import com.apettigrew.invoice.services.InvoiceService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.stream.Collectors;

@RestController
@RequestMapping(produces = JsonApiConstants.JSON_API_CONTENT_TYPE)
public class InvoiceController {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    private final InvoiceService invoiceService;

    public InvoiceController(InvoiceService invoiceService) {
        this.invoiceService = invoiceService;
    }

    @Autowired
    private ContactInfoDto contactInfoDto;

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public SingleResourceResponse<InvoiceResource> createInvoice(final @Valid @RequestBody CreateRequest<InvoiceCreateRequest> requestData) {
        InvoiceDto invoiceDto = requestData.getData().generateDto();
        Invoice savedInvoice = invoiceService.createInvoice(invoiceDto);

        return new SingleResourceResponse<>(InvoiceResource.toResource(savedInvoice));
    }

    @GetMapping
    public MultipleResourceResponse<InvoiceResource> getAllInvoices(
            @RequestHeader("ap-correlation-id")
            String correlationId,
            @PageableDefault(size = 10, direction = Sort.Direction.ASC) Pageable pageable,
            @RequestParam(value = "status", required = false) InvoiceStatus status) {

        logger.debug("ap-correlation-id found: {} ", correlationId);

        Page<Invoice> invoices = invoiceService.getAllInvoices(pageable,status);

        final Page<InvoiceResource> invoiceResourcePage = new PageImpl<>(
                invoices.getContent()
                        .stream()
                        .map(InvoiceResource::toResource)
                        .collect(Collectors.toList()),
                invoices.getPageable(),
                invoices.getTotalElements()
        );
        return new MultipleResourceResponse<>(invoiceResourcePage);
    }

    @GetMapping("/{id}")
    public SingleResourceResponse<InvoiceResource> getInvoiceById(final @PathVariable("id") String id) {
        Invoice invoice = invoiceService.getInvoiceById(id);
        return new SingleResourceResponse<>(InvoiceResource.toResource(invoice));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public SingleResourceResponse<InvoiceResource> updateInvoice(final @PathVariable String id, @RequestBody @Validated UpdateRequest<InvoiceCreateRequest> requestData) {
        InvoiceDto invoiceDto = requestData.getData().generateDto();

        Invoice updatedInvoice = invoiceService.updateInvoice(id, invoiceDto);
        return new SingleResourceResponse<>(InvoiceResource.toResource(updatedInvoice));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteInvoice(final @PathVariable String id) {
        try {
            invoiceService.deleteInvoice(id);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @GetMapping("/contact-info")
    public ResponseEntity<ContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contactInfoDto);
    }
}