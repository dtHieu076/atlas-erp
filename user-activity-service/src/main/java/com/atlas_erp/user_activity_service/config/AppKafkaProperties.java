package com.atlas_erp.user_activity_service.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "app.kafka")
@Data
public class AppKafkaProperties {
    private Topic topic;

    @Data
    public static class Topic {
        private String userActivityLog;
    }
}