package com.atlas_erp.user_activity_service.config;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import io.micrometer.common.util.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.config.AbstractMongoClientConfiguration;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.TimeZone;

@Configuration
public class MongoConfig extends AbstractMongoClientConfiguration {

    @Value("${spring.mongodb.uri:}")
    private String connectionString;

    @Override
    protected String getDatabaseName() {
        return "logging_db";
    }

    @Override
    @Bean
    public MongoClient mongoClient() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC")); // Chuẩn UTC cho Instant
        if (StringUtils.isNotBlank(connectionString)) {
            return MongoClients.create(connectionString);
        }
        return super.mongoClient();
    }

    @Bean
    public MongoTemplate mongoTemplate() {
        return new MongoTemplate(mongoClient(), getDatabaseName());
    }

    // Comment: Cấu hình hỗ trợ lưu Instant chuẩn xác với timezone UTC
}
