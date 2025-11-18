package com.apettigrew.invoice.dtos;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class InvoiceItemDto {

    private int id;

    @NotBlank(message = "Item name is required")
    @Size(min=10, max = 255, message = "Item name must be between 10 and 255 characters")
    private String name;

    @NotNull(message = "Quantity is required")
    @Min(value = 1, message = "Quantity must be at least 1")
    private Integer quantity;

    @NotNull(message = "Price is required")
    @DecimalMin(value = "0.00", inclusive = true, message = "Price must be at least 0.00")
    @Digits(integer = 9, fraction = 2, message = "Price format is invalid (e.g., 123456789.90)")
    private BigDecimal price;

    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private BigDecimal total;
}

