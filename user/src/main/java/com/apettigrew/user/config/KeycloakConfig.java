package com.apettigrew.user.config;

import lombok.AllArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@AllArgsConstructor
@EnableConfigurationProperties(KeycloakConfigProperties.class)
public class KeycloakConfig {

    static KeycloakConfigProperties keycloakConfigProperties;
    static Keycloak keycloak = null;


    public static Keycloak getKeycloakInstance() {

        if(keycloak == null) {
            keycloak = KeycloakBuilder
                    .builder()
                    .serverUrl(keycloakConfigProperties.getServerUrl())
                    .realm(keycloakConfigProperties.getRealm())
                    .clientId(keycloakConfigProperties.getClientId())
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(keycloakConfigProperties.getUserName())
                    .password(keycloakConfigProperties.getPassword()).build();
        }
        return keycloak;
    }
}