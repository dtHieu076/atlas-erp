package com.atlas_erp.user_activity_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import java.util.List;

@Configuration
public class CorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        // Chú ý: Dùng AllowedOrigins thay vì Patterns để dễ nhận diện hơn với localhost
        configuration.setAllowedOrigins(List.of("http://localhost:3000",
                "https://login-chi-dusky.vercel.app",
                "https://hrfrontendv1.netlify.app"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS", "PATCH"));
        configuration.setAllowedHeaders(List.of("*"));
        configuration.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);

        // Điểm khác biệt mấu chốt: Trả về một CorsFilter
        return new CorsFilter(source);
    }
}