package com.atlas_erp.hr_service.Cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

/**
 * Redis template configuration for Spring Data Redis 4.0.
 * <p>
 * Uses simple {@link StringRedisSerializer} for keys and values. JSON
 * serialization/deserialization is handled explicitly in the
 * {@code RedisCacheService} layer using the globally‑configured
 * {@link com.fasterxml.jackson.databind.ObjectMapper}, which provides:
 * <ul>
 * <li>{@link com.fasterxml.jackson.datatype.jsr310.JavaTimeModule}
 * for Java 8 dates</li>
 * <li>disabled {@code WRITE_DATES_AS_TIMESTAMPS} for readable JSON</li>
 * <li>safe polymorphic typing via
 * {@link com.fasterxml.jackson.databind.jsontype.BasicPolymorphicTypeValidator}</li>
 * </ul>
 * <p>
 * This design decouples Redis serialization from the cache service logic,
 * avoiding deprecated/removed Spring Data Redis APIs and giving explicit
 * control over JSON handling.
 * <p>
 * 🧹 **Important:** after deploying this change you **must flush Redis** (e.g.
 * <code>redis-cli FLUSHDB</code>) because existing entries may use incompatible
 * formats.
 */
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);
        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(new StringRedisSerializer());
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(new StringRedisSerializer());
        template.afterPropertiesSet();
        return template;
    }
}