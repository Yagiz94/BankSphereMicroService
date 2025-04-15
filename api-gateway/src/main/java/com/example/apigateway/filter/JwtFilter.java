package com.example.apigateway.filter;

import com.example.apigateway.service.JwtRedisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextImpl;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Collections;

@Component
@Order(-200) // Ensure it runs very early in the filter chain
public class JwtFilter implements WebFilter {

    private final JwtRedisService jwtRedisService;

    public JwtFilter(JwtRedisService jwtRedisService) {
        this.jwtRedisService = jwtRedisService;
        System.out.println("✅ JwtGlobalFilter initialized!");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        System.out.println("JwtFilter: Received request for " + path);

        // 1. Bypass filtering for public endpoints (e.g. registration and login)
        if (path.equals("/api/user/register")) {
            System.out.println("JwtFilter: Bypassing filtering for public endpoint " + path);
            return chain.filter(exchange);
        }

        // 2. Extract the Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            System.out.println("JwtFilter: Missing or invalid Authorization header.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap("Authorization header is missing or invalid.".getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        String jwtToken = authHeader.substring(7); // Remove "Bearer " prefix
        System.out.println("JwtFilter: Received token: " + jwtToken);

        // 3. Extract the username from the JWT token
        String username = extractUsernameFromToken(jwtToken);
        if (username == null) {
            System.out.println("JwtFilter: Unable to extract username from token.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap("Invalid token.".getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        // 4. Retrieve the stored secret key (token) from Redis for this user
        String secretKeyBase64 = jwtRedisService.retrieveSecretKey(username);
        if (secretKeyBase64 == null) {
            System.out.println("JwtFilter: Secret key not found for user");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap("Secret key not found for user.".getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        String storedToken = new String(Base64.getDecoder().decode(secretKeyBase64), StandardCharsets.UTF_8);
        System.out.println("JwtFilter: Extracted token is: " + storedToken);
        if (!storedToken.equals(jwtToken)) {
            System.out.println("JwtFilter: Token mismatch or expired token for user");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap("Invalid or expired token.".getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        System.out.println("JwtFilter: Token validation completed.");
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        // Optionally, you can add user information to the request (for example, in headers) if needed.
        return chain.filter(exchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
    }

    /**
     * Extracts the username from the JWT by decoding its payload.
     * This method uses basic Base64 decoding and JSON parsing.
     */
    private String extractUsernameFromToken(String token) {
        try {
            // Split the token (header.payload.signature)
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid token format.");
            }
            // Decode the payload (the second part)
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

            // Parse the JSON to extract the username claim
            ObjectMapper mapper = new ObjectMapper();
            JsonNode payloadNode = mapper.readTree(payloadJson);
            String username = payloadNode.has("username") ? payloadNode.get("username").asText() : null;

            if (username == null) {
                throw new IllegalArgumentException("User name claim not found in token.");
            }

            // Retrieve the secret key for this user from the database
            String secretKeyBase64 = jwtRedisService.retrieveSecretKey(username);
            if (secretKeyBase64 == null) {
                throw new IllegalArgumentException("Secret key not found for the user");
            }

            // If validation succeeds, return the extracted username
            return username;
        } catch (ExpiredJwtException e) {
            System.out.println("Token has expired.");
        } catch (JwtException e) {
            System.out.println("JWT error: " + e.getMessage());
        } catch (Exception e) {
            System.out.println("Error extracting username from token: " + e.getMessage());
        }
        return null; // Return null if any error occurs
    }
}

