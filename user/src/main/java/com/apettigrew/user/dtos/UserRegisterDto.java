package com.apettigrew.user.dtos;


import com.apettigrew.user.jsonapi.ResourceDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserRegisterDto implements ResourceDto<UUID> {
    @NotNull(message = "Username is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String email;

    @NotNull(message = "First name is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String userName;

    @NotNull(message = "First name is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String firstName;

    @NotNull(message = "First name is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String lastName;

    @NotNull(message = "Password is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String password;

//    @NotNull(message = "Role is required")
//    @Size(max = 255, message ="This field can only be 255 characters long")
//    private String role;

//    @NotNull(message = "birthDate is required")
//    private LocalDate birthDate;
}
