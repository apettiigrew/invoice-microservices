package com.apettigrew.invoice.command;

import com.apettigrew.invoice.dtos.AddressDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class CreateInvoiceCommand {

    @TargetAggregateIdentifier
    private final String id;
    private final LocalDate paymentDue;
    private final String description;
    private final Integer paymentTerms;
    private final String clientName;
    private final String clientEmail;
    private final AddressDto senderAddress;
    private final AddressDto clientAddress;
    private final String status;
    private final BigDecimal total;
    private final boolean activeSw;
}
