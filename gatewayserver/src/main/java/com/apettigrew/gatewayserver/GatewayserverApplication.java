package com.apettigrew.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.filter.ratelimit.KeyResolver;
import org.springframework.cloud.gateway.filter.ratelimit.RedisRateLimiter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Objects;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route(p -> p
					.path("/api/v1/invoices", "/api/v1/invoices/**")
					.filters( f -> f.rewritePath("/api/v1/invoices/?(?<segment>.*)","/${segment}")
							.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
							.circuitBreaker(config->config.setName("invoicesCircuitBreaker"))
							.retry((retryConfig ->
									retryConfig.setRetries(2)
									.setMethods(HttpMethod.GET)
											.setBackoff(Duration.ofMillis(100),Duration.ofMillis(1000),2,true))))

					.uri("lb://INVOICES"))
				.route(p -> p
					.path("/api/v1/users/**")
					.filters( f -> f.rewritePath("/api/v1/users/(?<segment>.*)","/${segment}")
							.addResponseHeader("X-Response-Time", LocalDateTime.now().toString())
							.requestRateLimiter(config ->
									config.setRateLimiter(redisRateLimiter())
											.setKeyResolver(keyResolver())))
					.uri("lb://USERS")).build();
	}


	@Bean
	public RedisRateLimiter redisRateLimiter(){
		return new RedisRateLimiter(5,5,1);
	}

	@Bean
	public KeyResolver keyResolver(){
		return exchange -> Mono.justOrEmpty(Objects.requireNonNull(exchange.getRequest().getRemoteAddress()).getAddress().getHostAddress());
	}
}
