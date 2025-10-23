package com.apettigrew.user.controllers;

import com.apettigrew.user.dtos.KeycloakTokenDto;
import com.apettigrew.user.dtos.UserLoginDto;
import com.apettigrew.user.jsonapi.JsonApiConstants;
import com.apettigrew.user.jsonapi.requests.CreateRequest;
import com.apettigrew.user.jsonapi.requests.UserLoginRequest;
import com.apettigrew.user.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(value = "/api", produces = JsonApiConstants.JSON_API_CONTENT_TYPE)
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

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestParam String refreshToken) {
        try {
            String result = userService.logout(refreshToken);
            return ResponseEntity.ok(result);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Logout failed: " + e.getMessage());
        }
    }
}

