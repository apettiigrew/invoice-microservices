package com.apettigrew.invoice.command.controller;

import com.apettigrew.invoice.command.CreateInvoiceCommand;
import com.apettigrew.invoice.command.DeleteInvoiceCommand;
import com.apettigrew.invoice.command.UpdateInvoiceCommand;
import com.apettigrew.invoice.constant.ApplicationConstants;
import com.apettigrew.invoice.controllers.InvoiceController;
import com.apettigrew.invoice.dtos.ContactInfoDto;
import com.apettigrew.invoice.dtos.InvoiceDto;
import com.apettigrew.invoice.entities.Invoice;
import com.apettigrew.invoice.jsonapi.InvoiceResource;
import com.apettigrew.invoice.jsonapi.JsonApiConstants;
import com.apettigrew.invoice.jsonapi.ResponseDto;
import com.apettigrew.invoice.jsonapi.SingleResourceResponse;
import com.apettigrew.invoice.jsonapi.requests.CreateRequest;
import com.apettigrew.invoice.jsonapi.requests.InvoiceCreateRequest;
import com.apettigrew.invoice.jsonapi.requests.UpdateRequest;
import com.apettigrew.invoice.services.InvoiceService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.api.Http;
import jakarta.validation.Valid;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping(value = "/api", produces = JsonApiConstants.JSON_API_CONTENT_TYPE)
public class InvoiceCommandController {
    private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

    private final InvoiceService invoiceService;

    private final CommandGateway commandGateway;

    @Value("${build.version}")
    private String buildVersion;

    @Autowired
    private Environment environment;

    @Autowired
    private ContactInfoDto contactInfoDto;

    public InvoiceCommandController(InvoiceService invoiceService, CommandGateway commandGateway) {
        this.commandGateway = commandGateway;
        this.invoiceService = invoiceService;
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public ResponseEntity<ResponseDto> createInvoice(final @Valid @RequestBody CreateRequest<InvoiceCreateRequest> requestData) throws JsonProcessingException {

        InvoiceDto invoiceDto = requestData.getData().generateDto();
        CreateInvoiceCommand createInvoiceCommand = CreateInvoiceCommand.builder()
                .id(UUID.randomUUID().toString())
                .paymentDue(invoiceDto.getPaymentDue())
                .status(invoiceDto.getStatus())
                .description(invoiceDto.getDescription())
                .paymentTerms(invoiceDto.getPaymentTerms())
                .clientName(invoiceDto.getClientName())
                .clientEmail(invoiceDto.getClientEmail())
                .senderAddress(invoiceDto.getSenderAddress())
                .clientAddress(invoiceDto.getClientAddress())
                .total(invoiceDto.getTotal())
                .activeSw(ApplicationConstants.ACTIVE_SW)
                .build();

        commandGateway.sendAndWait(createInvoiceCommand);
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(new ResponseDto(HttpStatus.CREATED.toString(), ApplicationConstants.MESSAGE_201));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public ResponseEntity<ResponseDto> updateUser(final @PathVariable String id, @RequestBody @Validated UpdateRequest<InvoiceCreateRequest> requestData) {
        InvoiceDto invoiceDto = requestData.getData().generateDto();

        UpdateInvoiceCommand updateInvoiceCommand = UpdateInvoiceCommand.builder()
                .id(invoiceDto.getId())
                .paymentDue(invoiceDto.getPaymentDue())
                .status(invoiceDto.getStatus())
                .description(invoiceDto.getDescription())
                .paymentTerms(invoiceDto.getPaymentTerms())
                .clientName(invoiceDto.getClientName())
                .clientEmail(invoiceDto.getClientEmail())
                .senderAddress(invoiceDto.getSenderAddress())
                .clientAddress(invoiceDto.getClientAddress())
                .total(invoiceDto.getTotal())
                .activeSw(ApplicationConstants.ACTIVE_SW)
                .build();

        commandGateway.sendAndWait(updateInvoiceCommand);
        return ResponseEntity
                .status(HttpStatus.ACCEPTED)
                .body(new ResponseDto(HttpStatus.ACCEPTED.toString(), ApplicationConstants.MESSAGE_200));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<ResponseDto> deleteUser(final @PathVariable String id) {

        DeleteInvoiceCommand deleteInvoiceCommand = DeleteInvoiceCommand.builder()
                    .id(id).activeSw(ApplicationConstants.IN_ACTIVE_SW).build();

        commandGateway.sendAndWait(deleteInvoiceCommand);
        return ResponseEntity.status(HttpStatus.OK)
                .body(new ResponseDto(HttpStatus.OK.toString(), ApplicationConstants.MESSAGE_200));
    }
}
