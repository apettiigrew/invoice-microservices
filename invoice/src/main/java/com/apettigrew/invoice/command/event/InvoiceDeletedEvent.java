package com.apettigrew.invoice.command.event;

import lombok.Data;

@Data
public class InvoiceDeletedEvent {
    private String id;
    private boolean activeSw;
}
