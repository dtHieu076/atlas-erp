package com.atlas_erp.organization_service.Config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable()) // Tắt CSRF (vì đang test API)
                .authorizeHttpRequests(auth -> auth
                        .anyRequest().permitAll() // Cho phép toàn bộ request
                );

        return http.build();
    }
}
