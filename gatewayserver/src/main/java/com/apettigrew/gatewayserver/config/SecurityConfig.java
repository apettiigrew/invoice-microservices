package com.apettigrew.gatewayserver.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverterAdapter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    private static final Logger logger = LoggerFactory.getLogger(SecurityConfig.class);

    @Bean
    public SecurityWebFilterChain springSecurityFilterChain(ServerHttpSecurity serverHttpSecurity) {
        logger.info("Configuring security filter chain");
        
        serverHttpSecurity
                .authorizeExchange(exchanges ->
                        exchanges
                                // Public endpoint - no authentication required
                                .pathMatchers("/api/v1/users/auth/**").permitAll()
                                // All other routes require authentication
                                .anyExchange().authenticated()
                )
                .oauth2ResourceServer(oAuth2ResourceServerSpec ->
                        oAuth2ResourceServerSpec
                                .jwt(jwtSpec -> {
                                    jwtSpec.jwtAuthenticationConverter(grantedAuthoritiesExtractor());
                                })
                                .authenticationEntryPoint((exchange, exception) -> {
                                    logger.error("JWT Authentication failed for path {}: {} - {}", 
                                        exchange.getRequest().getPath(), 
                                        exception.getClass().getSimpleName(),
                                        exception.getMessage());
                                    if (logger.isDebugEnabled()) {
                                        logger.debug("Full exception stack trace:", exception);
                                    }
                                    return Mono.error(exception);
                                })
                )
                .csrf(csrfSpec -> csrfSpec.disable());

        return serverHttpSecurity.build();
    }

    private Converter<Jwt, Mono<AbstractAuthenticationToken>> grantedAuthoritiesExtractor() {
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(new KeycloakRoleConverter());
        jwtAuthenticationConverter.setPrincipalClaimName("preferred_username");
        return new ReactiveJwtAuthenticationConverterAdapter(jwtAuthenticationConverter);
    }

}
