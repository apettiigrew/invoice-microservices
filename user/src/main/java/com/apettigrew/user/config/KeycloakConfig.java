package com.apettigrew.user.config;

import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {

    private final KeycloakConfigProperties keycloakConfigProperties;  // Inject properties

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakConfigProperties.getServerUrl())
                .realm(keycloakConfigProperties.getRealm())
                .clientId(keycloakConfigProperties.getClientId())
                .grantType(OAuth2Constants.PASSWORD)
                .username(keycloakConfigProperties.getUserName())
                .password(keycloakConfigProperties.getPassword())
                .build();
    }
}