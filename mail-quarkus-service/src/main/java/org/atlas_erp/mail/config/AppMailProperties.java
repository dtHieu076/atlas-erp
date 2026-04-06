package org.atlas_erp.mail.config;

import io.smallrye.config.ConfigMapping;
import io.smallrye.config.WithDefault;

/**
 * Application mail properties configuration.
 * Maps from app.mail.* properties in application.properties
 */
@ConfigMapping(prefix = "app.mail")
public interface AppMailProperties {
    
    @WithDefault("100")
    int batchSize();
    
    @WithDefault("3")
    int maxRetries();
    
    @WithDefault("mail_requests")
    String topic();
}
