package com.apettigrew.user.jsonapi.requests;

import com.apettigrew.user.ResourceTypes;
import com.apettigrew.user.dtos.UserDto;
import com.apettigrew.user.jsonapi.resources.UpdateResource;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UserUpdateRequest implements UpdateResource<UserDto> {

    @Pattern(regexp = ResourceTypes.USERS)
    @NotNull
    private final String type = ResourceTypes.USERS;

    @Valid
    @NotNull
    private UserDto attributes;

    @Override
    public UserDto generateDto() {
        return attributes;
    }
}
