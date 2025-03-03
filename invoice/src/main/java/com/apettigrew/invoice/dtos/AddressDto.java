package com.apettigrew.invoice.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class AddressDto {
    @NotBlank(message = "Street is required")
    @Size(max = 255, message = "Street cannot exceed 255 characters")
    private String street;

    @NotBlank(message = "City is required")
    @Size(max = 255, message = "City cannot exceed 255 characters")
    private String city;

    @NotBlank(message = "State is required")
    @Size(max = 255, message = "State cannot exceed 255 characters")
    private String state;

    @NotBlank(message = "Zip code is required")
    @Size(max = 10, message = "Zip code cannot exceed 10 characters") // Adjust as needed
    private String zip;
}