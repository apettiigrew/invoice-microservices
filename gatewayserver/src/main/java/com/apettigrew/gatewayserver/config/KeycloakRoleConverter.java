package com.apettigrew.gatewayserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class KeycloakRoleConverter implements Converter<Jwt, Collection<GrantedAuthority>> {

    private static final Logger logger = LoggerFactory.getLogger(KeycloakRoleConverter.class);

    /**
     * Function used to extract the keycloak roles in a format
     * @param source JWT token
     * @return Collection of granted authorities
     */
    @Override
    public Collection<GrantedAuthority> convert(Jwt source) {
        try {
            Map<String, Object> realmAccess = (Map<String, Object>) source.getClaims().get("realm_access");
            
            if (realmAccess == null || realmAccess.isEmpty()) {
                logger.debug("No realm_access found in JWT token");
                return new ArrayList<>();
            }
            
            // Safely check if roles exist and is a list
            Object rolesObj = realmAccess.get("roles");
            if (rolesObj == null) {
                logger.debug("No roles found in realm_access");
                return new ArrayList<>();
            }
            
            if (!(rolesObj instanceof List)) {
                logger.warn("roles in realm_access is not a List, type: {}", rolesObj.getClass().getName());
                return new ArrayList<>();
            }
            
            @SuppressWarnings("unchecked")
            List<String> roles = (List<String>) rolesObj;
            
            if (roles.isEmpty()) {
                logger.debug("Roles list is empty");
                return new ArrayList<>();
            }
            
            Collection<GrantedAuthority> authorities = roles.stream()
                    .map(roleName -> "ROLE_" + roleName.toUpperCase())
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
            
            logger.debug("Extracted {} roles from JWT token", authorities.size());
            return authorities;
        } catch (Exception e) {
            logger.error("Error extracting roles from JWT token: {}", e.getMessage(), e);
            // Return empty list instead of throwing exception to allow authentication to proceed
            // Authentication can succeed even without roles
            return new ArrayList<>();
        }
    }

}
