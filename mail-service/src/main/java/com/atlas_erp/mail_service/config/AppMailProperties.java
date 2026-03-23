package com.atlas_erp.mail_service.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.mail")
public class AppMailProperties {
    private int batchSize = 100;
    private int maxRetries = 3;
    private String topic = "mail_requests";
}
