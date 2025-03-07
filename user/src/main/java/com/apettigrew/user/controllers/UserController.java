package com.apettigrew.user.controllers;

import com.apettigrew.user.ResourceTypes;
import com.apettigrew.user.dtos.UserDto;
import com.apettigrew.user.dtos.UserRegisterDto;
import com.apettigrew.user.entities.User;
import com.apettigrew.user.jsonapi.JsonApiConstants;
import com.apettigrew.user.jsonapi.MultipleResourceResponse;
import com.apettigrew.user.jsonapi.SingleResourceResponse;
import com.apettigrew.user.jsonapi.UserResource;
import com.apettigrew.user.jsonapi.requests.CreateRequest;
import com.apettigrew.user.jsonapi.requests.UpdateRequest;
import com.apettigrew.user.jsonapi.requests.UserCreateRequest;
import com.apettigrew.user.jsonapi.requests.UserUpdateRequest;
import com.apettigrew.user.services.UserService;
import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api", produces = JsonApiConstants.JSON_API_CONTENT_TYPE)
@AllArgsConstructor
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public MultipleResourceResponse<UserResource> getAllUsers(@PageableDefault(size = 10, sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable) {
        Page<User> users = userService.getAllUsers(pageable);

        final Page<UserResource> userResourcePage = new PageImpl<>(
                users.getContent()
                        .stream()
                        .map(UserResource::toResource)
                        .collect(Collectors.toList()),
                users.getPageable(),
                users.getTotalElements()
        );
        return new MultipleResourceResponse<>(userResourcePage);
    }

    @GetMapping("/{uuid}")
    public SingleResourceResponse<UserResource> getUserByUuid(final @PathVariable("uuid") UUID uuid) {
        User user = userService.getUserByUuid(uuid);
        return new SingleResourceResponse<>(UserResource.toResource(user));
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public SingleResourceResponse<UserResource> createUser(final @RequestBody @Validated CreateRequest<UserCreateRequest> requestData) {
        UserRegisterDto registerUserDto = requestData.getData().generateDto();
        final var user = userService.createUser(registerUserDto);

        return new SingleResourceResponse<>(UserResource.toResource(user));
    }

    @PatchMapping("/{uuid}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public SingleResourceResponse<UserResource> updateUser(final @PathVariable UUID uuid, @RequestBody @Validated UpdateRequest<UserUpdateRequest> userDto) {
        User updatedUser = userService.updateUser(uuid, userDto.getData().generateDto());
        return new SingleResourceResponse<>(UserResource.toResource(updatedUser));
    }

    @DeleteMapping("/{uuid}")
    @ResponseStatus(code = HttpStatus.NO_CONTENT)
    public ResponseEntity<Void> deleteUser(final @PathVariable UUID uuid) {
        try {
            userService.deleteUser(uuid);
            return ResponseEntity.noContent().build();
        } catch (RuntimeException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
