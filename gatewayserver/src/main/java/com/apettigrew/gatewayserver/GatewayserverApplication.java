package com.apettigrew.gatewayserver;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;

import java.time.LocalDateTime;

@SpringBootApplication
public class GatewayserverApplication {

	public static void main(String[] args) {
		SpringApplication.run(GatewayserverApplication.class, args);
	}

	@Bean
	public RouteLocator customRouteLocator(RouteLocatorBuilder routeLocatorBuilder) {
		return routeLocatorBuilder.routes()
				.route(p -> p
					.path("/api/v1/invoices/**")
					.filters( f -> f.rewritePath("/petti/invoices/(?<segment>.*)","/${segment}")
							.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
					.uri("lb://INVOICES"))
				.route(p -> p
					.path("/api/v1/users/**")
					.filters( f -> f.rewritePath("/petti/users/(?<segment>.*)","/${segment}")
							.addResponseHeader("X-Response-Time", LocalDateTime.now().toString()))
					.uri("lb://USERS")).build();
	}
}
