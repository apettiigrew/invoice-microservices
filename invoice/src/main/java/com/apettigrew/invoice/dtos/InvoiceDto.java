package com.apettigrew.invoice.dtos;

import com.apettigrew.invoice.enums.InvoiceStatus;
import com.apettigrew.invoice.jsonapi.ResourceDto;
import com.apettigrew.invoice.validation.ValidInvoiceStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceDto implements ResourceDto<UUID> {
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private int id;

    @NotNull(message = "Payment due date is required")
    @Future(message = "Payment due date must be in the future")
    private LocalDate paymentDue;

    @NotBlank(message = "Description is required")
    @Size(min=16, max = 255, message = "Description needs to be between 16 and 255 characters")
    private String description;

    @NotNull(message = "Payment terms are required")
    @Min(value = 1, message = "Payment terms must be non-negative")
    @Max(value = 6, message = "Payment terms cannot be more than 6 months")
    private Integer paymentTerms;

    @NotBlank(message = "Client name is required")
    @Size(max = 255, message = "Client name cannot exceed 255 characters")
    private String clientName;

    @NotBlank(message = "Client email is required")
    @Email(message = "Invalid client email format")
    @Size(max = 255, message = "Client email cannot exceed 255 characters")
    private String clientEmail;

    @Valid
    @NotNull(message = "Sender address is required")
    private AddressDto senderAddress;

    @Valid
    @NotNull(message = "Client address is required")
    private AddressDto clientAddress;

    @NotNull(message = "Status is required")
    @ValidInvoiceStatus
    private InvoiceStatus status;

    @NotNull(message = "Total is required")
    @DecimalMin(value = "0.00", message = "Total must be at least 0.00")
    @Digits(integer = 8, fraction = 2, message = "Total format is invalid (e.g., 12345678.90)") // Adjust integer part as needed
    private BigDecimal total;
}