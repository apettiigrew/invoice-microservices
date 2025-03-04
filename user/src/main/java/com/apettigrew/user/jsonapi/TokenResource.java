package com.apettigrew.user.jsonapi;

import com.apettigrew.user.ResourceTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@ToString
@AllArgsConstructor
public class TokenResource {
    private final String type = ResourceTypes.AUTH;

    private LoginResponseDto attributes;

    public static TokenResource toResource(final LoginResponseDto loginResponseDto){
        if(loginResponseDto == null){
            return null;
        }

        final var modelMapper = new ModelMapper();
        final var attributes = modelMapper.map(loginResponseDto,LoginResponseDto.class);

        return new TokenResource(attributes);
    }
}
