package com.apettigrew.user.config;

import jakarta.ws.rs.client.ClientBuilder;
import lombok.RequiredArgsConstructor;
import org.jboss.resteasy.client.jaxrs.ResteasyClientBuilder;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;

import java.util.concurrent.TimeUnit;

@Configuration
@RequiredArgsConstructor
public class KeycloakConfig {
    private static final Logger logger = LoggerFactory.getLogger(KeycloakConfig.class);
    private final KeycloakConfigProperties keycloakConfigProperties;

    @Bean
    public RestClient restClient(RestClient.Builder builder, KeycloakConfigProperties keycloakConfigProperties) {
        return builder.baseUrl(keycloakConfigProperties.getAuthServerUrl()).build();
    }

    @Bean
    public Keycloak keycloak() {

        return KeycloakBuilder.builder()
                .serverUrl(keycloakConfigProperties.getServerUrl())
                .realm(keycloakConfigProperties.getRealm())
                .clientId(keycloakConfigProperties.getClientId())
                .grantType(OAuth2Constants.PASSWORD)
                .username(keycloakConfigProperties.getUserName())
                .password(keycloakConfigProperties.getPassword())
                .resteasyClient(((ResteasyClientBuilder) ClientBuilder.newBuilder())
                        .connectionPoolSize(3)
                        .connectionTTL(10, TimeUnit.SECONDS)
                        .build())
                .build();
    }
}