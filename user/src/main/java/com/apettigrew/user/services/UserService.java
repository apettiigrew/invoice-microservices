package com.apettigrew.user.services;

import com.apettigrew.user.config.KeycloakConfigProperties;
import com.apettigrew.user.dtos.KeycloakTokenDto;
import com.apettigrew.user.dtos.UserDto;
import com.apettigrew.user.dtos.UserRegisterDto;
import com.apettigrew.user.exceptions.UserAlreadyExistsException;
import jakarta.ws.rs.ClientErrorException;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClient;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    private final Keycloak keycloak;
    private final KeycloakConfigProperties keycloakConfigProperties; // Inject config properties
    private final RestClient restClient;

    @Autowired
    @Qualifier("skipNullModelMapper")
    private ModelMapper modelMapper;

    public KeycloakTokenDto login(String username, String password) {

        String tokenUrl = keycloakConfigProperties.getServerUrl() + "/realms/" + keycloakConfigProperties.getRealm() + "/protocol/openid-connect/token";

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", OAuth2Constants.PASSWORD);
        map.add("client_id", keycloakConfigProperties.getAppClientId());
        map.add("client_secret", keycloakConfigProperties.getAppClientSecret());
        map.add("username", username);
        map.add("password", password);
        map.add("scope","openid email profile");
        try {
            var response =  restClient.post()
                .uri(tokenUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(map)
                .retrieve()
                .toEntity(KeycloakTokenDto.class);

            return response.getBody();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void logout(String refreshToken) {
        String logoutUrl = keycloakConfigProperties.getServerUrl() + "/realms/" + keycloakConfigProperties.getRealm() + "/protocol/openid-connect/logout";

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", keycloakConfigProperties.getAppClientId());
        String clientSecret = keycloakConfigProperties.getAppClientSecret();
        if (clientSecret != null && !clientSecret.isBlank()) {
            map.add("client_secret", clientSecret);
        }
        map.add("refresh_token", refreshToken);

        try {
            var response = restClient.post()
                    .uri(logoutUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(map)
                    .retrieve()
                    .toBodilessEntity();

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Logout failed with status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            logger.error("Failed to logout user from Keycloak", e);
            throw new RuntimeException("Logout failed", e);
        }
    }

    public KeycloakTokenDto refreshToken(String refreshToken) {
        String tokenUrl = keycloakConfigProperties.getServerUrl() + "/realms/" + keycloakConfigProperties.getRealm() + "/protocol/openid-connect/token";

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("grant_type", "refresh_token");
        map.add("client_id", keycloakConfigProperties.getAppClientId());
        String clientSecret = keycloakConfigProperties.getAppClientSecret();
        if (clientSecret != null && !clientSecret.isBlank()) {
            map.add("client_secret", clientSecret);
        }
        map.add("refresh_token", refreshToken);

        try {
            var response = restClient.post()
                    .uri(tokenUrl)
                    .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                    .body(map)
                    .retrieve()
                    .toEntity(KeycloakTokenDto.class);

            if (!response.getStatusCode().is2xxSuccessful()) {
                throw new RuntimeException("Token refresh failed with status: " + response.getStatusCode());
            }

            return response.getBody();
        } catch (Exception e) {
            logger.error("Failed to refresh token in Keycloak", e);
            throw new RuntimeException("Token refresh failed", e);
        }
    }

    public UserDto getKeycloakUserbyId(String id) {
        UserRepresentation userRep = keycloak.realm(keycloakConfigProperties.getRealm()).users().get(id).toRepresentation();
        return modelMapper.map(userRep, UserDto.class);
    }

    public List<UserDto> getAllUsers(Pageable pageable) {
        int first = (int) pageable.getOffset();
        int max = pageable.getPageSize();
        List<UserRepresentation> userRepresentations = keycloak.realm(keycloakConfigProperties.getRealm()).users().list(first, max);

        List<UserDto> users = new ArrayList<>();
        for (UserRepresentation userRep : userRepresentations) {
            UserDto userDto = modelMapper.map(userRep, UserDto.class);
            users.add(userDto);
        }

        return users;
    }

    public UserDto createUser(UserRegisterDto userDto) {
        UserRepresentation userRep = new UserRepresentation();
        // Keycloak requires username - using email as username is a common pattern
        userRep.setUsername(userDto.getEmail());
        userRep.setFirstName(userDto.getFirstName());
        userRep.setLastName(userDto.getLastName());
        userRep.setEmail(userDto.getEmail());
        userRep.setEnabled(true);
        userRep.setEmailVerified(true);
        List<CredentialRepresentation> creds = new ArrayList<>();
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setValue(userDto.getPassword());
        cred.setType(CredentialRepresentation.PASSWORD);
        creds.add(cred);
        userRep.setCredentials(creds);

        Response response = null;
        try {
            response = keycloak.realm(keycloakConfigProperties.getRealm())
                    .users()
                    .create(userRep);

            int status = response.getStatus();

            switch (status) {
                case 201: // Created
                    String userId = CreatedResponseUtil.getCreatedId(response);
                    UserRepresentation createdUser = keycloak.realm(keycloakConfigProperties.getRealm())
                            .users()
                            .get(userId)
                            .toRepresentation();
                    return modelMapper.map(createdUser, UserDto.class);

                case 409: // Conflict
                    logger.error("User already exists with email: {}", userDto.getEmail());
                    throw new UserAlreadyExistsException("User with this email already exists");

                default:
                    // Try to read error response body for more details
                    String errorBody = null;
                    try {
                        if (response.hasEntity()) {
                            errorBody = response.readEntity(String.class);
                        }
                    } catch (Exception ex) {
                        logger.debug("Could not read error response body", ex);
                    }
                    String errorMessage = "Failed to create user. Keycloak returned status: " + status;
                    if (errorBody != null) {
                        errorMessage += ". Error details: " + errorBody;
                    }
                    logger.error(errorMessage);
                    throw new RuntimeException(errorMessage);
            }

        } catch (ClientErrorException e) {
            int status = e.getResponse() != null ? e.getResponse().getStatus() : 0;

            if (status == 409) {
                logger.error("User already exists with email: {}", userDto.getEmail());
                throw new UserAlreadyExistsException("User with this email already exists", e);
            }

            // Try to read error response body for more details
            String errorBody = null;
            try {
                if (e.getResponse() != null && e.getResponse().hasEntity()) {
                    errorBody = e.getResponse().readEntity(String.class);
                }
            } catch (Exception ex) {
                logger.debug("Could not read error response body from ClientErrorException", ex);
            }
            
            String errorMessage = "Keycloak client error creating user: Status " + status;
            if (errorBody != null) {
                errorMessage += ". Error details: " + errorBody;
            }
            logger.error(errorMessage, e);
            throw new RuntimeException("Failed to create user: " + (errorBody != null ? errorBody : e.getMessage()), e);

        } catch (Exception e) {
            logger.error("Unexpected error creating user in Keycloak: {}", e.getMessage(), e);
            throw new RuntimeException("Failed to create user: " + e.getMessage(), e);

        } finally {
            if (response != null) {
                response.close();
            }
        }
    }

    public UserDto getUserById(String id){
        UserRepresentation userRep = keycloak.realm(keycloakConfigProperties.getRealm()).users().get(id).toRepresentation();
        return modelMapper.map(userRep, UserDto.class);
    }

    public UserDto updateUser(String id,UserDto userDto){
        UserRepresentation userRep = keycloak.realm(keycloakConfigProperties.getRealm()).users().get(id).toRepresentation();
        userRep.setFirstName(userDto.getFirstName());
        userRep.setLastName(userDto.getLastName());
        userRep.setEmail(userDto.getEmail());
        try {
            keycloak.realm(keycloakConfigProperties.getRealm()).users().get(id).update(userRep);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return modelMapper.map(userRep, UserDto.class);
    }
}
