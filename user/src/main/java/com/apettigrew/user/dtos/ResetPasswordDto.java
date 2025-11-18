package com.apettigrew.user.dtos;

import com.apettigrew.user.jsonapi.ResourceDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class ResetPasswordDto implements ResourceDto<UUID> {
    @NotNull(message = "Token is required")
    @Size(max = 1000, message = "This field can only be 1000 characters long")
    private String token;

    @NotNull(message = "Email is required")
    @jakarta.validation.constraints.Email(message = "Email must be valid")
    @Size(max = 255, message = "This field can only be 255 characters long")
    private String email;

    @NotNull(message = "New password is required")
    @Size(min = 8, max = 255, message = "Password must be between 8 and 255 characters")
    private String newPassword;
}

