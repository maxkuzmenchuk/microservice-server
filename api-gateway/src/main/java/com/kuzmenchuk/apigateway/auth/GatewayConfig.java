package com.kuzmenchuk.apigateway.auth;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {

    private final ApiGatewayJwtFilter filter;

    public GatewayConfig(ApiGatewayJwtFilter filter) {
        this.filter = filter;
    }

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("authService", r -> r.path("/auth/**").uri("http://localhost:8081"))
                .route("userManagementService", r -> r.path("/users/**").filters(f -> f.filter(filter)).uri("http://localhost:8082"))
                .route("paymentsService", r -> r.path("/payments/**").filters(f -> f.filter(filter)).uri("http://localhost:8083"))
                .build();
    }

}
