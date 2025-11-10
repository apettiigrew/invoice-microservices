package com.apettigrew.user.jsonapi.requests;

import com.apettigrew.user.ResourceTypes;
import com.apettigrew.user.dtos.ForgotPasswordDto;
import com.apettigrew.user.jsonapi.resources.CreateResource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ForgotPasswordRequest implements CreateResource<ForgotPasswordDto> {

    @Pattern(regexp = ResourceTypes.AUTH)
    @NotNull
    private final String type = ResourceTypes.AUTH;

    @Valid
    @NotNull
    private ForgotPasswordDto attributes;

    @Override
    public ForgotPasswordDto generateDto() {
        return attributes;
    }
}

