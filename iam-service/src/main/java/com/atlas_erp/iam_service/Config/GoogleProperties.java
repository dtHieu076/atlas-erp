package com.atlas_erp.iam_service.Config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import lombok.Data;

@Configuration
@ConfigurationProperties(prefix = "google")
@Data
public class GoogleProperties {
    private String clientId;
    private String issuer;
    private String jwksUri;
}
