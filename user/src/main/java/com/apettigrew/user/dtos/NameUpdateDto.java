package com.apettigrew.user.dtos;

import com.apettigrew.user.jsonapi.ResourceDto;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class NameUpdateDto implements ResourceDto<String> {
    @NotNull(message = "First name is required")
    @Size(max = 255, message = "This field can only be 255 characters long")
    private String firstName;

    @NotNull(message = "Last name is required")
    @Size(max = 255, message = "This field can only be 255 characters long")
    private String lastName;
}

