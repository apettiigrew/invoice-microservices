package com.apettigrew.user.config;

import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;

public class KeycloakConfig {

    static Keycloak keycloak = null;
    final static String serverUrl = "http://localhost:7080";
    public final static String realm = "development";
    final static String clientId = "admin-cli";
    final static String userName = "admin";
    final static String password = "1234";

    public KeycloakConfig() {}

    public static Keycloak getKeycloakInstance() {
        if(keycloak == null) {
            keycloak = KeycloakBuilder
                    .builder()
                    .serverUrl(serverUrl)
                    .realm(realm)
                    .clientId(clientId)
                    .grantType(OAuth2Constants.PASSWORD)
                    .username(userName)
                    .password(password).build();
        }
        return keycloak;
    }
}