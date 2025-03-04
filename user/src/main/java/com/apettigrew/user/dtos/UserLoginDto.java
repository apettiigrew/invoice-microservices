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
public class UserLoginDto implements ResourceDto<UUID> {
    @NotNull(message = "Username is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String username;

    @NotNull(message = "Password is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String password;
}
