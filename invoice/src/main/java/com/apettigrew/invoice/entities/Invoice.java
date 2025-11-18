package com.apettigrew.invoice.entities;

import com.apettigrew.invoice.enums.InvoiceStatus;
import com.apettigrew.invoice.validation.ValidInvoiceStatus;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@ToString
@Data
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(name = "user_id", nullable = false)
    private String userId;

    @Column(name = "payment_due", nullable = false)
    @NotNull(message = "Payment due date is required")
    @Future(message = "Payment due date must be in the future")
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate paymentDue;

    @Column(nullable = false, columnDefinition = "TEXT")
    @NotBlank(message = "Description is required")
    @Size(min=10, max = 255, message = "Description needs to be between 10 and 255 characters")
    private String description;

    @Column(name = "payment_terms", nullable = false)
    @NotNull(message = "Payment terms are required")
    @Min(value = 1, message = "Payment terms must be non-negative")
    @Max(value = 24, message = "Payment terms cannot be more than 24 months")
    private Integer paymentTerms;

    @Column(name = "client_name", nullable = false)
    @NotBlank(message = "Client name is required")
    @Size(min=3,max = 255, message = "Client name needs to be between 3 and 255 characters")
    private String clientName;

    @Column(name = "client_email", nullable = false)
    @NotBlank(message = "Client email is required")
    @Email(message = "Invalid client email format")
    private String clientEmail;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Status is required")
    @ValidInvoiceStatus
    private InvoiceStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    @NotNull(message = "Total is required")
    private BigDecimal total;

    @Column(name = "sender_address", columnDefinition = "jsonb")
    @NotNull(message = "Sender address is required")
    @NotBlank(message = "Sender address is required")
    private String senderAddress;

    @Column(name = "client_address", columnDefinition = "jsonb")
    @NotNull(message = "Client address is required")
    @NotBlank(message = "Client address is required")
    private String clientAddress;

    @Column(name="created_at")
    @CreationTimestamp
    private LocalDateTime createdAt;

    @Column(name="updated_at")
    @UpdateTimestamp
    private LocalDateTime updatedAt;

    @Column(name="deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private LocalDateTime deletedAt;

    @OneToMany(mappedBy = "invoice", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @Valid
    @NotNull(message = "Invoice items are required")
    @Size(min = 1, message = "Invoice must have at least 1 invoice item")
    private List<InvoiceItem> invoiceItems;
}