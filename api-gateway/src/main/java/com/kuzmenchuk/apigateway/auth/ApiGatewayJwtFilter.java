package com.kuzmenchuk.apigateway.auth;

import com.kuzmenchuk.apigateway.auth.exception.JwtTokenMalformedException;
import com.kuzmenchuk.apigateway.auth.exception.JwtTokenMissingException;
import io.jsonwebtoken.Claims;
import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

@Component
public class ApiGatewayJwtFilter implements GatewayFilter {
    private final JwtUtil jwtUtil;

    public ApiGatewayJwtFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        ServerHttpRequest request = (ServerHttpRequest) exchange.getRequest();

        final List<String> apiEndpoints = Arrays.asList("/authorization", "/registration");

        Predicate<ServerHttpRequest> isApiSecured = r -> apiEndpoints.stream()
                .noneMatch(uri -> r.getURI().getPath().contains(uri));

        if (isApiSecured.test(request)) {
            if (!request.getHeaders().containsKey("Authorization")) {
                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.UNAUTHORIZED);

                return response.setComplete();
            }

            final String tokenHeader = request.getHeaders().getOrEmpty("Authorization").get(0);
            String jwtToken = tokenHeader.substring(7);
            try {
                jwtUtil.validateToken(jwtToken);
            } catch (JwtTokenMalformedException | JwtTokenMissingException e) {
                // e.printStackTrace();

                ServerHttpResponse response = exchange.getResponse();
                response.setStatusCode(HttpStatus.BAD_REQUEST);

                return response.setComplete();
            }

            Claims claims = jwtUtil.getClaims(jwtToken);
            exchange.getRequest().mutate().header("id", String.valueOf(claims.get("id"))).build();
        }

        return chain.filter(exchange);
    }
}
