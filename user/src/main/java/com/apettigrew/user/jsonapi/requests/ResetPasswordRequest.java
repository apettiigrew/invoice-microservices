package com.apettigrew.user.jsonapi.requests;

import com.apettigrew.user.ResourceTypes;
import com.apettigrew.user.dtos.ResetPasswordDto;
import com.apettigrew.user.jsonapi.resources.CreateResource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ResetPasswordRequest implements CreateResource<ResetPasswordDto> {

    @Pattern(regexp = ResourceTypes.AUTH)
    @NotNull
    private final String type = ResourceTypes.AUTH;

    @Valid
    @NotNull
    private ResetPasswordDto attributes;

    @Override
    public ResetPasswordDto generateDto() {
        return attributes;
    }
}

