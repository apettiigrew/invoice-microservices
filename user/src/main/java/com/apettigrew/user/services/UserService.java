package com.apettigrew.user.services;

import com.apettigrew.user.config.KeycloakConfigProperties;
import com.apettigrew.user.dtos.KeycloakTokenDto;
import com.apettigrew.user.dtos.UserDto;
import com.apettigrew.user.dtos.UserRegisterDto;
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
        userRep.setUsername(userDto.getUserName());
        userRep.setFirstName(userDto.getFirstName());
        userRep.setLastName(userDto.getLastName());
        userRep.setEmail(userDto.getEmail());
        userRep.setEnabled(true);
        userRep.setEmailVerified(true);
        List<CredentialRepresentation> creds = new ArrayList<>();
        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setValue(userDto.getPassword());
        creds.add(cred);
        userRep.setCredentials(creds);

        try  {
            Response response = keycloak.realm(keycloakConfigProperties.getRealm()).users().create(userRep);
            CreatedResponseUtil.getCreatedId(response);
            // User created successfully
        } catch(Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return modelMapper.map(userRep, UserDto.class);
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

    public String logout(String refreshToken) {
        String revokeUrl = keycloakConfigProperties.getServerUrl() + "/realms/" + keycloakConfigProperties.getRealm() + "/protocol/openid-connect/revoke";

        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("client_id", keycloakConfigProperties.getAppClientId());
        map.add("client_secret", keycloakConfigProperties.getAppClientSecret());
        map.add("token", refreshToken);
        map.add("token_type_hint", "refresh_token");

        try {
            restClient.post()
                .uri(revokeUrl)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(map)
                .retrieve()
                .toEntity(String.class);

            return "User logged out successfully.";
        } catch (Exception e) {
            logger.error("Logout failed: {}", e.getMessage());
            throw new RuntimeException("Logout failed: " + e.getMessage());
        }
    }
}
