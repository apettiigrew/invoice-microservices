package com.apettigrew.user.services;

import com.apettigrew.user.config.KeycloakConfig;
import com.apettigrew.user.controllers.UserController;
import com.apettigrew.user.dtos.UserDto;
import com.apettigrew.user.dtos.UserRegisterDto;
import com.apettigrew.user.entities.User;
import com.apettigrew.user.respositories.UserRepository;
import jakarta.ws.rs.core.Response;
import org.keycloak.admin.client.CreatedResponseUtil;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.resource.RealmResource;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.admin.client.resource.UsersResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;

import static com.apettigrew.user.config.KeycloakConfig.realm;

@Service
public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);

    @Autowired
    private UserRepository userRepository;

    @Autowired
    @Qualifier("skipNullModelMapper")
    private ModelMapper modelMapper;

    public Page<User> getAllUsers(Pageable pageable ) {
        return userRepository.findAll(pageable);
    }

    public User getUserByUuid(UUID uuid) {
        return userRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("User not found"));
    }

    public User createUser(UserRegisterDto userDto) {
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
        return modelMapper.map(userRep, User.class);
    }

    public User registerUser(UserRegisterDto userRegisterDto) {
        final User user =  modelMapper.map(userRegisterDto, User.class);
        return userRepository.save(user);
    }

    public User updateUser(UUID uuid, UserDto userDetails) {
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("User not found"));
        modelMapper.map(userDetails,user);
        return userRepository.save(user);
    }

    public void deleteUser(UUID uuid) {
        final var today = new Date();
        User user = userRepository.findByUuid(uuid).orElseThrow(() -> new RuntimeException("User not found"));
       user.setDeletedAt(today);
        userRepository.save(user);
    }
}
