package com.apettigrew.user.services;

import com.apettigrew.user.config.KeycloakConfig;
import com.apettigrew.user.dtos.UserDto;
import com.apettigrew.user.dtos.UserRegisterDto;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.apettigrew.user.config.KeycloakConfig.realm;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    @Qualifier("skipNullModelMapper")
    private ModelMapper modelMapper;

    public UserDto getKeycloakUserbyId(String id) {
        Keycloak keycloak = KeycloakConfig.getKeycloakInstance();
        UserRepresentation userRep = keycloak.realm(realm).users().get(id).toRepresentation();
        return modelMapper.map(userRep, UserDto.class);
    }

    public List<UserDto> getAllUsers(Pageable pageable) {
        int first = (int) pageable.getOffset();
        int max = pageable.getPageSize();

        Keycloak keycloak = KeycloakConfig.getKeycloakInstance();
        List<UserRepresentation> userRepresentations = keycloak.realm(realm).users().list(first, max);

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

        Keycloak keycloak = KeycloakConfig.getKeycloakInstance();
        try  {
            Response response = keycloak.realm(realm).users().create(userRep);
            String userId = CreatedResponseUtil.getCreatedId(response);
            UserResource userResource = keycloak.realm(realm).users().get(userId);
        }catch(Exception e){
            logger.error(e.getMessage());
            throw new RuntimeException(e.getMessage());
        }
        return modelMapper.map(userRep, UserDto.class);
    }

    public UserDto getUserById(String id){
        Keycloak keycloak = KeycloakConfig.getKeycloakInstance();
        UserRepresentation userRep = keycloak.realm(realm).users().get(id).toRepresentation();
        return modelMapper.map(userRep, UserDto.class);
    }

    public UserDto updateUser(String id,UserDto userDto){
        Keycloak keycloak = KeycloakConfig.getKeycloakInstance();
        UserRepresentation userRep = keycloak.realm(realm).users().get(id).toRepresentation();
        userRep.setFirstName(userDto.getFirstName());
        userRep.setLastName(userDto.getLastName());
        userRep.setEmail(userDto.getEmail());
        try {
            keycloak.realm(realm).users().get(id).update(userRep);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        return modelMapper.map(userRep, UserDto.class);
    }
}
