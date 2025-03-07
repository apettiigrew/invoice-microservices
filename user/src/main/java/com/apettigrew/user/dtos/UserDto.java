package com.apettigrew.user.dtos;

import com.apettigrew.user.jsonapi.ResourceDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.Date;
import java.util.UUID;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserDto implements ResourceDto<UUID> {
    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "First name is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String firstName;

    @NotNull(message = "First name is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String lastName;

    @NotNull(message = "Role is required")
    @Size(max = 255, message = "This field can only be 255 characters long")
    private String role;

    @NotNull(message = "birthDate is required")
    private LocalDate birthDate;

    @JsonIgnore
    private Date createdAt;
    private Date updatedAt;
    private Date deletedAt;
}
