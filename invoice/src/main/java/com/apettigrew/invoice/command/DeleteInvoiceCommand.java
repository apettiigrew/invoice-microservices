package com.apettigrew.invoice.command;


import lombok.Builder;
import lombok.Data;
import org.axonframework.modelling.command.TargetAggregateIdentifier;


@Data
@Builder
public class DeleteInvoiceCommand {
    @TargetAggregateIdentifier
    private final String id;
    private final boolean activeSw;
}
