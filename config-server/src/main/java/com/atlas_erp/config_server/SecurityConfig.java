package com.atlas_erp.config_server;

import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // 1. Tắt CSRF vì đây là giao tiếp giữa các Service (Stateless)
                .csrf(csrf -> csrf.disable())

                // 2. Cấu hình quyền truy cập
                .authorizeHttpRequests(auth -> auth
                        // Nếu bạn có trang web giới thiệu hoặc check health công khai
                        .requestMatchers("/api/public/**", "/actuator/health").permitAll()
                        // Tất cả các request lấy config (như /hr-service/default) PHẢI đăng nhập
                        .anyRequest().authenticated())

                // 3. Sử dụng Basic Auth (truyền user/pass qua Header)
                .httpBasic(Customizer.withDefaults());

        return http.build();
    }
}
