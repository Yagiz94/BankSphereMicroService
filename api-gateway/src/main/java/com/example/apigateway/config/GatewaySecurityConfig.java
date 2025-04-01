// GatewaySecurityConfig.java
package com.example.apigateway.config;

import com.example.apigateway.security.JwtAuthenticationEntryPoint;
import com.example.apigateway.security.JwtRequestFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class GatewaySecurityConfig {

    @Autowired
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Autowired
    private JwtRequestFilter customJwtFilter;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
//        http
//                .csrf().disable() // disable CSRF for stateless APIs
//                .exceptionHandling()
//                .authenticationEntryPoint(jwtAuthenticationEntryPoint)
//                .and()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .authorizeRequests()
//                .antMatchers("/public/**", "/actuator/**").permitAll()
//                .anyRequest().authenticated();

        http
                .httpBasic(httpBasic -> httpBasic.disable())
                // Disable CSRF protection for stateless APIs
                .csrf(csrf -> csrf.disable())
                // Configure exception handling
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                // Set session management to stateless
                .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Configure public endpoints and require authentication for others
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/api/user/register", "/api/user/login", "/api/user/authenticate", "/public/**").permitAll()
                        .anyRequest().authenticated()
                );

        // Add the custom JWT filter before the UsernamePasswordAuthenticationFilter
        http.addFilterBefore(customJwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
