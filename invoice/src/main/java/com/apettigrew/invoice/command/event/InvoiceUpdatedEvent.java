package com.apettigrew.invoice.command.event;

import com.apettigrew.invoice.dtos.AddressDto;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class InvoiceUpdatedEvent {
    private Long id;
    private LocalDate paymentDue;
    private String description;
    private Integer paymentTerms;
    private String clientName;
    private String clientEmail;
    private AddressDto senderAddress;
    private AddressDto clientAddress;
    private String status;
    private BigDecimal total;
    private boolean activeSw;
}
