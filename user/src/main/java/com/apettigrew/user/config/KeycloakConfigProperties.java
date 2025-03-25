package com.apettigrew.user.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Getter
@Setter
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakConfigProperties {
    private String serverUrl;
    private String realm;
    private String clientId;
    private String userName;
    private String password;
}

