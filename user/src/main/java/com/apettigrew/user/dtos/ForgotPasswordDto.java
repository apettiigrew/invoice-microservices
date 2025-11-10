package com.apettigrew.user.dtos;

import com.apettigrew.user.jsonapi.ResourceDto;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ForgotPasswordDto implements ResourceDto<UUID> {
    @NotNull(message = "Email is required")
    @Email(message = "Email must be valid")
    @Size(max = 255, message = "This field can only be 255 characters long")
    private String email;
}

