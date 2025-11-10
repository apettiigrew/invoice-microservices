package com.apettigrew.user.controllers;

import com.apettigrew.user.dtos.KeycloakTokenDto;
import com.apettigrew.user.dtos.UserLoginDto;
import com.apettigrew.user.dtos.UserRegisterDto;
import com.apettigrew.user.jsonapi.JsonApiConstants;
import com.apettigrew.user.jsonapi.SingleResourceResponse;
import com.apettigrew.user.jsonapi.requests.CreateRequest;
import com.apettigrew.user.jsonapi.requests.UserCreateRequest;
import com.apettigrew.user.jsonapi.requests.UserLoginRequest;
import com.apettigrew.user.jsonapi.resources.UserResource;
import com.apettigrew.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping(value = "/auth", produces = JsonApiConstants.JSON_API_CONTENT_TYPE)
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    @PostMapping("/login")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<KeycloakTokenDto> loginUsers(final @RequestBody @Validated CreateRequest<UserLoginRequest> loginRequest) {
        UserLoginDto userLoginDto = loginRequest.getData().generateDto();
        String username = userLoginDto.getUsername();
        String password = userLoginDto.getPassword();

        KeycloakTokenDto token = userService.login(username, password);
        return new ResponseEntity<>(token,HttpStatus.OK);
    }

    @PostMapping("/register")
    @ResponseStatus(code = HttpStatus.CREATED)
    public SingleResourceResponse<UserResource> createUser(final @RequestBody @Validated CreateRequest<UserCreateRequest> requestData) {
        UserRegisterDto registerUserDto = requestData.getData().generateDto();
        final var user = userService.createUser(registerUserDto);

        return new SingleResourceResponse<>(UserResource.toResource(user));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(@RequestParam("refresh_token") String refreshToken) {
        userService.logout(refreshToken);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/refresh")
    @ResponseStatus(code = HttpStatus.OK)
    public ResponseEntity<KeycloakTokenDto> refreshToken(@RequestParam("refresh_token") String refreshToken) {
        KeycloakTokenDto token = userService.refreshToken(refreshToken);
        return new ResponseEntity<>(token, HttpStatus.OK);
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

