package com.apettigrew.user.controllers;

import com.apettigrew.user.dtos.ContactInfoDto;
import com.apettigrew.user.dtos.UserDto;
import com.apettigrew.user.jsonapi.JsonApiConstants;
import com.apettigrew.user.jsonapi.SingleResourceResponse;
import com.apettigrew.user.jsonapi.requests.UpdateRequest;
import com.apettigrew.user.jsonapi.requests.UserUpdateRequest;
import com.apettigrew.user.jsonapi.resources.UserResource;
import com.apettigrew.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(produces = JsonApiConstants.JSON_API_CONTENT_TYPE)
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ContactInfoDto contactInfoDto;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping("/me")
    public SingleResourceResponse<UserResource> getUserByUuid(final @PathVariable("id") String id) {
        UserDto user = userService.getUserById(id);
        return new SingleResourceResponse<>(UserResource.toResource(user));
    }

    @PatchMapping("/{id}")
    @ResponseStatus(code = HttpStatus.ACCEPTED)
    public SingleResourceResponse<UserResource> updateUser(final @PathVariable String id, @RequestBody @Validated UpdateRequest<UserUpdateRequest> userDto) {
        var user = userDto.getData().generateDto();
        UserDto updatedUser = userService.updateUser(id,user);
        return new SingleResourceResponse<>(UserResource.toResource(updatedUser));
    }

    @GetMapping("/contact-info")
    public ResponseEntity<ContactInfoDto> getContactInfo() {
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(contactInfoDto);
    }


    @GetMapping("/context")
    public Map<String, String> getUserContext(
            @RequestHeader("X-User-Id") String userId,
            @RequestHeader("X-Username") String username,
            @RequestHeader(value = "X-Roles", required = false) String roles
    ) {
        return Map.of(
                "userId", userId,
                "username", username,
                "roles", roles
        );
    }

}
