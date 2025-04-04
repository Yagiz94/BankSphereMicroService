package com.example.apigateway.security;

import org.springframework.cloud.gateway.filter.GatewayFilter;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

@Component
public class RouteFilter implements GlobalFilter {

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        // Check the request path or any other condition before rerouting
        String requestPath = exchange.getRequest().getPath().toString();

        // Example: If the path is "/api/user/login", return a response immediately
        if (requestPath.equals("/api/user/login")) {
            // Send a response before routing
            exchange.getResponse().setStatusCode(HttpStatus.OK);
            exchange.getResponse().getHeaders().set("Custom-Header", "Response-Immediately");
            return exchange.getResponse().setComplete(); // Complete the request with a response
        }

        // Continue with the next filter (rerouting to the intended service)
        return chain.filter(exchange);
    }
}