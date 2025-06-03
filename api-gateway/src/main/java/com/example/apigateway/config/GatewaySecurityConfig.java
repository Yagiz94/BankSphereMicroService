package com.example.apigateway.config;

import com.example.apigateway.filter.JwtFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.context.NoOpServerSecurityContextRepository;


@Configuration
public class GatewaySecurityConfig {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http, JwtFilter jwtFilter) {
        return http
                // Disable CSRF for stateless APIs
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                // Configure exception handling to return 401 Unauthorized
                .exceptionHandling(exceptions ->
                        exceptions.authenticationEntryPoint((exchange, ex) -> {
                            exchange.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                            return exchange.getResponse().setComplete();
                        }))
                // Do not store a SecurityContext (stateless)
                .securityContextRepository(NoOpServerSecurityContextRepository.getInstance())
                // Define authorization rules: allow public access only for registration
                .authorizeExchange(authz ->
                        authz
                                .pathMatchers("/api/user/register").permitAll()
                                .pathMatchers("/actuator/health").permitAll() // permit for service health checks
                                .anyExchange().authenticated())
                // Add the custom JWT authentication filter to the chain at the authentication phase.
                .addFilterAt(jwtFilter, SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }
}
