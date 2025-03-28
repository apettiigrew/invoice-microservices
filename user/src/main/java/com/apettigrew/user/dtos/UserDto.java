package com.apettigrew.user.dtos;

import com.apettigrew.user.jsonapi.ResourceDto;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
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
public class UserDto implements ResourceDto<UUID> {
    @JsonIgnore
    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String id;

    @NotNull(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;

    @NotNull(message = "First name is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String firstName;

    @NotNull(message = "First name is required")
    @Size(max = 255, message ="This field can only be 255 characters long")
    private String lastName;
}
