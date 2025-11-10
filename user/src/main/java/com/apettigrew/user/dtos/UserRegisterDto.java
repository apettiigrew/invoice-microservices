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
public class UserRegisterDto implements ResourceDto<UUID> {
    @NotNull(message = "Email is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String email;

    @NotNull(message = "First name is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String lastName;

    @NotNull(message = "Password is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String password;
}
