package com.atlas_erp.hr_service.Cache.serviceCache;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.Collections;
import java.util.List;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class RedisCacheService {
    private final RedisTemplate<String, String> redisTemplate;
    private final ObjectMapper objectMapper;

    /**
     * Put any object into Redis as JSON with TTL in minutes.
     */
    public <T> void put(String key, T value, long ttlMinutes) {
        try {
            String json = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, json, Duration.ofMinutes(ttlMinutes));
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize object to JSON", e);
        }
    }

    /**
     * Retrieve a JSON value from Redis and convert it to the requested type.
     */
    public <T> T get(String key, Class<T> clazz) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                return null;
            }
            return objectMapper.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Convenience helper for lists: deserializes JSON and returns the typed list.
     * Never returns null; returns empty list on cache miss.
     */
    public <T> List<T> getList(String key, Class<T> elementClass) {
        try {
            String json = redisTemplate.opsForValue().get(key);
            if (json == null) {
                return Collections.emptyList();
            }
            return objectMapper.readValue(
                    json,
                    objectMapper.getTypeFactory().constructCollectionType(List.class, elementClass));
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize list from JSON: " + e.getMessage(), e);
        }
    }

    /**
     * Check if a key exists in Redis.
     */
    public boolean exists(String key) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(key));
    }

    /**
     * Delete a key from Redis.
     */
    public void delete(String key) {
        redisTemplate.delete(key);
    }

    /**
     * Delete all keys matching the given pattern.
     * ⚠️ Note: This uses KEYS scan which can be expensive on large datasets.
     * Consider using SCAN pattern for production systems.
     */
    public void deleteByPattern(String pattern) {
        Set<String> keys = redisTemplate.keys(pattern);
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}
