package com.apettigrew.user.jsonapi.resources;

import com.apettigrew.user.ResourceTypes;
import com.apettigrew.user.dtos.UserDto;
import com.apettigrew.user.jsonapi.Resource;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;
import org.modelmapper.ModelMapper;

@Getter
@ToString
@AllArgsConstructor
public class UserResource implements Resource<UserDto> {
    private final String type = ResourceTypes.USERS;
    private String id;
    private UserDto attributes;

    public static UserResource toResource(final UserDto userDto){
        if(userDto == null){
            return null;
        }

        final var modelMapper = new ModelMapper();
        final var attributes = modelMapper.map(userDto,UserDto.class);

        return new UserResource(userDto.getId(), attributes);
    }
}
