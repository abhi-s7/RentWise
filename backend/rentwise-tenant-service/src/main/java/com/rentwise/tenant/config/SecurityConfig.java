package com.rentwise.tenant.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
    
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // Allow all API calls for now - security will be added later with JWT
                        // Note: /api/tenants/{id}/assign-property should be ADMIN only when JWT is implemented
                        .requestMatchers("/api/**").permitAll()
                        .anyRequest().permitAll()
                );
        
        return http.build();
    }
}

