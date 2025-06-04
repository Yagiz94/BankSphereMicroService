package com.example.apigateway.filter;

import com.example.apigateway.service.JwtRedisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.server.reactive.ServerHttpRequest;
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
@Order(-200) // Ensure the JwtFilter runs very early in the filter chain
public class JwtFilter implements WebFilter {

    private final JwtRedisService jwtRedisService;
    private static final Logger logger = LogManager.getLogger(JwtFilter.class);

    public JwtFilter(JwtRedisService jwtRedisService) {
        this.jwtRedisService = jwtRedisService;
        logger.info("JwtGlobalFilter initialized!");
    }

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, WebFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();
        logger.info("JwtFilter: Received request for " + path);

        // 1. Bypass filtering for public endpoints (e.g. registration and login)
        if (path.equals("/api/user/register")) {
            logger.info("JwtFilter: Bypassing filtering for public endpoint " + path);
            return chain.filter(exchange);
        }

        // 2. Extract the Authorization header
        String authHeader = exchange.getRequest().getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            logger.error("JwtFilter: Missing or invalid Authorization header.");
            logger.error("Authorization header is missing or invalid.".getBytes(StandardCharsets.UTF_8));
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap("Authorization header is missing or invalid.".getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }
        String jwtToken = authHeader.substring(7); // Remove "Bearer " prefix

        // 3. Extract the username from the JWT token
        String username = extractUsernameFromToken(jwtToken);
        if (username == null) {
            logger.error("JwtFilter: Unable to extract username from token.");
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            logger.error("Invalid token.".getBytes(StandardCharsets.UTF_8));
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap("Invalid token.".getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        // 4. Retrieve the stored secret key (token) from Redis for this user
        String secretKeyBase64 = jwtRedisService.retrieveSecretKey(username);
        if (secretKeyBase64 == null) {
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            logger.error("JwtFilter: Secret key not found for user.".getBytes(StandardCharsets.UTF_8));
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap("Secret key not found for user.".getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        // 5. Using the stored secretKey, retrieve the token and compare it with the received token
        String storedToken = new String(Base64.getDecoder().decode(secretKeyBase64), StandardCharsets.UTF_8);
        if (!storedToken.equals(jwtToken)) {
            logger.error("JwtFilter: Token mismatch or expired token for user");
            logger.error("Invalid or expired token.".getBytes(StandardCharsets.UTF_8));
            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
            DataBuffer buffer = exchange.getResponse().bufferFactory()
                    .wrap("Invalid or expired token.".getBytes(StandardCharsets.UTF_8));
            return exchange.getResponse().writeWith(Mono.just(buffer));
        }

        logger.info("JwtFilter: Token validation completed.");
        Authentication authentication = new UsernamePasswordAuthenticationToken(username, null, Collections.emptyList());
        SecurityContext securityContext = new SecurityContextImpl(authentication);

        // Now, mutate the request to add the "userName" header with the username
        // By mutating the exchange to add the header with the username, you're effectively “injecting” that information into every request processed by downstream handlers.
        // This ensures that your controller method receives the userName header (or any other header you need) that matches the username corresponding to the token stored in Redis.
        ServerHttpRequest mutatedRequest = exchange.getRequest().mutate()
                .header("userName", username)
                .build();
        ServerWebExchange mutatedExchange = exchange.mutate().request(mutatedRequest).build();

        // Optionally, you can add user information to the request (for example, in headers) if needed.
        return chain.filter(mutatedExchange).contextWrite(ReactiveSecurityContextHolder.withSecurityContext(Mono.just(securityContext)));
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
                logger.error("Invalid token format.");
                throw new IllegalArgumentException("Invalid token format.");
            }
            // Decode the payload (the second part)
            String payloadJson = new String(Base64.getUrlDecoder().decode(parts[1]), StandardCharsets.UTF_8);

            // Parse the JSON to extract the username claim
            ObjectMapper mapper = new ObjectMapper();
            JsonNode payloadNode = mapper.readTree(payloadJson);
            String username = payloadNode.has("username") ? payloadNode.get("username").asText() : null;

            if (username == null) {
                logger.error("User name claim not found in token.");
                throw new IllegalArgumentException("User name claim not found in token.");
            }

            // Retrieve the secret key for this user from the database
            String secretKeyBase64 = jwtRedisService.retrieveSecretKey(username);
            if (secretKeyBase64 == null) {
                logger.error("Secret key not found for the user");
                throw new IllegalArgumentException("Secret key not found for the user");
            }

            // If validation succeeds, return the extracted username
            return username;
        } catch (ExpiredJwtException e) {
            logger.error("Token has expired.");
        } catch (JwtException e) {
            logger.error("JWT error: " + e.getMessage());
        } catch (Exception e) {
            logger.error("Error extracting username from token: " + e.getMessage());
        }
        return null; // Return null if any error occurs
    }
}

