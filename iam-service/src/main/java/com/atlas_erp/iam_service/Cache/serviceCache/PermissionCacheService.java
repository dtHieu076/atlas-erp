package com.atlas_erp.iam_service.Cache.serviceCache;

import com.atlas_erp.iam_service.Dto.Response.Permission.MyPermissionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PermissionCacheService {

    private static final Duration TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, Object> redisTemplate;

    private String key(UUID userId) {
        return "iam:permission:" + userId;
    }

    /**
     * Get cached permission of user
     */
    public MyPermissionResponse getMyPermission(UUID userId) {
        return (MyPermissionResponse) redisTemplate
                .opsForValue()
                .get(key(userId));
    }

    /**
     * Cache permission of user
     */
    public void putMyPermission(UUID userId, MyPermissionResponse response) {
        redisTemplate.opsForValue().set(
                key(userId),
                response,
                TTL);
    }

    /**
     * Clear cache when permission/role changes
     */
    public void evict(UUID userId) {
        redisTemplate.delete(key(userId));
    }
}
