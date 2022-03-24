package com.kuzmenchuk.apigateway;

import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GatewayConfig {
    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("oauth-service", r -> r.path("/api/user-account/**")
                        .uri("http://localhost:8081"))
                .route("oauth-service", r -> r.path("/oauth/**")
                        .uri("http://localhost:8081"))
                .build();
    }

}
