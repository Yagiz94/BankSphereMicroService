package com.example.apigateway.security;

import com.example.apigateway.service.JwtRedisService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.*;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;


@Component
public class JwtRequestFilter implements Filter {
    @Autowired
    private JwtRedisService jwtRedisService;  // Inject JwtRedisService

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain)
            throws IOException, ServletException {

        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        // 1. Skip filtering for public endpoints like /api/test/** or /api/auth/register
        String requestUri = request.getRequestURI();
        System.out.println("Request URI: " + requestUri);
        if (requestUri.equals("/api/user/register")) {
            // Simply pass the request to the next filter in the chain
            filterChain.doFilter(servletRequest, servletResponse);
            return;
        }

        // 2. Extract and validate JWT
        String jwtToken = request.getHeader("Authorization");
        if (jwtToken != null && jwtToken.startsWith("Bearer ")) {
//            System.out.println("Received token: " + jwtToken);
            jwtToken = jwtToken.substring(7); // Strip "Bearer " prefix
            // Retrieve username from token
            String username = extractUsernameFromToken(jwtToken);
            // todo we may check if the userName exists in the database
            if (username != null) {
                // Retrieve the secret key from the database for this user
                String secretKeyBase64 = jwtRedisService.retrieveSecretKey(username);
                if (secretKeyBase64 != null) {
                    String storedToken = new String(Base64.getDecoder().decode(secretKeyBase64), StandardCharsets.UTF_8);
                    if (storedToken.equals(jwtToken)) {
                        // Token is valid, add the user to the security context
                        SecurityContextHolder.getContext().setAuthentication(
                                new UsernamePasswordAuthenticationToken(username, null, null));
                        System.out.println("User is authenticated.");
                    } else {
                        System.out.println("Invalid or expired token.");
                        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                        response.getWriter().write("Invalid or expired token.");
                        return;
                    }
                } else {
                    System.out.println("Secret key not found for user: " + username);
                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                    response.getWriter().write("Secret key not found for user.");
                    return;
                }
            } else {
                System.out.println("Invalid token - Unable to extract username.");
                response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                response.getWriter().write("Invalid token.");
                return;
            }
        } else {
            System.out.println("Authorization header is missing or invalid.");
            // If the token is missing or doesn't start with "Bearer"
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("Authorization header is missing or invalid.");
            return;
        }

        // Continue the filter chain if everything is valid
        filterChain.doFilter(servletRequest, servletResponse);
        System.out.println("Filter chain completed.");
    }

    private String extractUsernameFromToken(String token) {
        try {
            // Manually split the token into its parts (header, payload, signature)
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                throw new IllegalArgumentException("Invalid token format");
            }

            // Decode the payload (which is the second part) using Base64 URL decoding
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

        return null;
    }
}