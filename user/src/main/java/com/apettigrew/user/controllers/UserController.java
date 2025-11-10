package com.apettigrew.user.controllers;

import com.apettigrew.user.dtos.ContactInfoDto;
import com.apettigrew.user.dtos.UserDto;
import com.apettigrew.user.dtos.UserRegisterDto;
import com.apettigrew.user.jsonapi.JsonApiConstants;
import com.apettigrew.user.jsonapi.MultipleResourceResponse;
import com.apettigrew.user.jsonapi.SingleResourceResponse;
import com.apettigrew.user.jsonapi.requests.CreateRequest;
import com.apettigrew.user.jsonapi.requests.UpdateRequest;
import com.apettigrew.user.jsonapi.requests.UserCreateRequest;
import com.apettigrew.user.jsonapi.requests.UserUpdateRequest;
import com.apettigrew.user.jsonapi.resources.UserResource;
import com.apettigrew.user.services.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(produces = JsonApiConstants.JSON_API_CONTENT_TYPE)
@Validated
public class UserController {
    @Autowired
    private UserService userService;

    @Autowired
    private ContactInfoDto contactInfoDto;

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    @GetMapping
    public MultipleResourceResponse<UserResource> getAllUsers(@PageableDefault(size = 10, sort = "lastName", direction = Sort.Direction.ASC) Pageable pageable) {
        List<UserDto> userDto = userService.getAllUsers(pageable);
        List<UserResource> resources = userDto.stream().map(UserResource::toResource).collect(Collectors.toList());
        return new MultipleResourceResponse<>(resources);
    }

    @GetMapping("/{id}")
    public SingleResourceResponse<UserResource> getUserByUuid(final @PathVariable("id") String id) {
        UserDto user = userService.getUserById(id);
        return new SingleResourceResponse<>(UserResource.toResource(user));
    }

    @PostMapping
    @ResponseStatus(code = HttpStatus.CREATED)
    public SingleResourceResponse<UserResource> createUser(final @RequestBody @Validated CreateRequest<UserCreateRequest> requestData) {
        UserRegisterDto registerUserDto = requestData.getData().generateDto();
        System.out.println("Started");
        final var user = userService.createUser(registerUserDto);

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

//    @DeleteMapping("/{uuid}")
//    @ResponseStatus(code = HttpStatus.NO_CONTENT)
//    public ResponseEntity<Void> deleteUser(final @PathVariable UUID uuid) {
//        try {
//            userService.deleteUser(uuid);
//            return ResponseEntity.noContent().build();
//        } catch (RuntimeException e) {
//            return ResponseEntity.internalServerError().build();
//        }
//    }
}
