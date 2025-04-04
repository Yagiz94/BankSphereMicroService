// GatewaySecurityConfig.java
package com.example.apigateway.config;

import com.example.apigateway.security.AccessDeniedManager;
import com.example.apigateway.security.JwtAuthenticationEntryPoint;
import com.example.apigateway.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

/*
@Configuration
@EnableWebSecurity
public class GatewaySecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter customJwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
//                .httpBasic(httpBasic -> httpBasic.disable())
                // Disable CSRF protection for stateless APIs
                .csrf(csrf -> csrf.disable())
                // Configure exception handling
//                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                // Set session management to stateless
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configure public endpoints and require authentication for others
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/user/register", "/api/user/login", "/api/user/authenticate", "/public/**").permitAll()
                        .anyRequest().authenticated()
                );

        // Add the custom JWT filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
*/

@Configuration
@EnableWebSecurity
public class GatewaySecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter customJwtFilter;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("⚡ Security filter initialized");

        http
                .addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class)
                .httpBasic(httpBasic -> {
                    System.out.println("✅ Disabling HTTP Basic Authentication");
                    httpBasic.disable();
                })
                .csrf(csrf -> {
                    System.out.println("❌ CSRF is disabled");
                    csrf.csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse());
                    csrf.disable();
                })

                .exceptionHandling(ex -> {
                    System.out.println("✅ Configuring exception handling");
                    ex.accessDeniedHandler(new AccessDeniedManager());
                    ex.authenticationEntryPoint(jwtAuthenticationEntryPoint);
                })
                .sessionManagement(session -> {
                    System.out.println("🛑 Stateless session management enabled");
                    session.sessionCreationPolicy(SessionCreationPolicy.STATELESS);
                })
                .authorizeHttpRequests(authz -> {
                    System.out.println("✅ Configuring authentication rules");
                    authz.requestMatchers("/api/user/register", "/api/user/login", "/public/**").permitAll();
                });

        System.out.println("⚡ Security Context: " + SecurityContextHolder.getContext().getAuthentication());
        System.out.println("⚡ JwtRequestFilter has been added to the filter chain.");

        return http.build();
    }
}
