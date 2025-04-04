package com.example.apigateway.security;

import org.springframework.security.access.AccessDeniedException;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.io.IOException;
import java.util.logging.Logger;

public class AccessDeniedManager implements AccessDeniedHandler {
    private static final Logger logger = Logger.getLogger(AccessDeniedManager.class.getName());

    @Override
    public void handle(HttpServletRequest request, HttpServletResponse response,
                       AccessDeniedException accessDeniedException) throws IOException {
        // Log details about the denied access
        logger.warning("Access Denied: " + request.getRequestURI());
        logger.warning("User: " + (request.getUserPrincipal() != null ? request.getUserPrincipal().getName() : "Anonymous"));
        logger.warning("Exception: " + accessDeniedException.getMessage());

        // Optionally, redirect to custom access denied page
        response.sendRedirect("/access-denied");
    }
}