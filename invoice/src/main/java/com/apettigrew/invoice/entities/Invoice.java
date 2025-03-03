package com.apettigrew.invoice.entities;

import com.apettigrew.invoice.enums.InvoiceStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "invoices")
public class Invoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "payment_due", nullable = false)
    private LocalDate paymentDue;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String description;

    @Column(name = "payment_terms", nullable = false)
    private Integer paymentTerms;

    @Column(name = "client_name", nullable = false)
    private String clientName;

    @Column(name = "client_email", nullable = false)
    private String clientEmail;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private InvoiceStatus status;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal total;

    @Column(name = "sender_address", columnDefinition = "jsonb")
    private String senderAddress;

    @Column(name = "client_address", columnDefinition = "jsonb")
    private String clientAddress;

    @Column(name="created_at")
    @CreationTimestamp
    private Date createdAt;

    @Column(name="updated_at")
    @UpdateTimestamp
    private Date updatedAt;

    @Column(name="deleted_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedAt;
}