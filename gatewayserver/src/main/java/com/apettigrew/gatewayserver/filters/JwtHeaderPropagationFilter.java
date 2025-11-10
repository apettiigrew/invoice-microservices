package com.apettigrew.gatewayserver.filters;

import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;
import org.springframework.security.oauth2.jwt.Jwt;

@Component
public class JwtHeaderPropagationFilter implements GlobalFilter, Ordered {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, org.springframework.cloud.gateway.filter.GatewayFilterChain chain) {
        return ReactiveSecurityContextHolder.getContext()
                .map(context -> context.getAuthentication())
                .flatMap(auth -> {
                    if (auth != null && auth.getPrincipal() instanceof Jwt jwt) {
                        // Extract claims
                        final String userId = jwt.getClaimAsString("sub");
                        final String username = jwt.getClaimAsString("preferred_username");
                        final String roles = String.join(",", jwt.getClaimAsStringList("roles")); // or "scope"

                        // Add them as headers to be forwarded downstream
                        ServerWebExchange mutatedExchange = exchange.mutate()
                                .request(req -> req.headers(headers -> {
                                    headers.set("X-User-Id", userId);
                                    headers.set("X-Username", username);
                                    headers.set("X-Roles", roles);
                                }))
                                .build();
                        return chain.filter(mutatedExchange);
                    }
                    return chain.filter(exchange);
                })
                .switchIfEmpty(Mono.defer(() -> chain.filter(exchange)));
    }

    @Override
    public int getOrder() {
        // Run after authentication but before routing
        return Ordered.LOWEST_PRECEDENCE;
    }
}