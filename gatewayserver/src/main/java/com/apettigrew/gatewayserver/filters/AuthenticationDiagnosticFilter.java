package com.apettigrew.gatewayserver.filters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class AuthenticationDiagnosticFilter implements GlobalFilter, Ordered {

    private static final Logger logger = LoggerFactory.getLogger(AuthenticationDiagnosticFilter.class);

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        HttpHeaders headers = exchange.getRequest().getHeaders();
        String authHeader = headers.getFirst(HttpHeaders.AUTHORIZATION);
        String path = exchange.getRequest().getPath().toString();
        
        if (authHeader != null) {
            // Log that Authorization header is present (but not the token value for security)
            if (authHeader.startsWith("Bearer ")) {
                String tokenPrefix = authHeader.substring(0, Math.min(20, authHeader.length())) + "...";
                logger.info("Authorization header found for path {}: {}", path, tokenPrefix);
            } else {
                logger.warn("Authorization header found but doesn't start with 'Bearer ' for path {}: {}", 
                    path, authHeader.substring(0, Math.min(20, authHeader.length())));
            }
        } else {
            logger.warn("No Authorization header found for path: {}", path);
        }
        
        return chain.filter(exchange).doOnError(throwable -> {
            logger.error("Error processing request for path {}: {}", path, throwable.getMessage(), throwable);
        });
    }

    @Override
    public int getOrder() {
        // Run before security filters but after request trace filter
        return -100;
    }
}

