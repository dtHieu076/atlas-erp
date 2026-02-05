package com.atlas_erp.iam_service.Cache.serviceCache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.atlas_erp.iam_service.Cache.dtoCache.CachedUserIdentity;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserIdentityCacheService {

    private static final Duration TTL = Duration.ofMinutes(30);
    private final RedisTemplate<String, Object> redisTemplate;

    private String key(String provider, String providerUid) {
        return "iam:identity:" + provider + ":" + providerUid;
    }

    private String keyByUserId(UUID userId) {
        return "iam:identity:user:" + userId;
    }

    // ========== GET ==========

    public CachedUserIdentity get(String provider, String providerUid) {
        return (CachedUserIdentity) redisTemplate
                .opsForValue()
                .get(key(provider, providerUid));
    }

    public CachedUserIdentity getByUserId(UUID userId) {
        return (CachedUserIdentity) redisTemplate
                .opsForValue()
                .get(keyByUserId(userId));
    }

    // ========== PUT (single source of truth) ==========

    public void put(CachedUserIdentity identity) {

        // by provider + providerUid
        redisTemplate.opsForValue().set(
                key(identity.getProvider(), identity.getProviderUid()),
                identity,
                TTL);

        // by userId (for refresh token flow)
        redisTemplate.opsForValue().set(
                keyByUserId(identity.getUserId()),
                identity,
                TTL);
    }

    // ========== EVICT ==========

    public void evict(CachedUserIdentity identity) {
        redisTemplate.delete(key(identity.getProvider(), identity.getProviderUid()));
        redisTemplate.delete(keyByUserId(identity.getUserId()));
    }

    public void evictByUserId(UUID userId) {
        redisTemplate.delete(keyByUserId(userId));
    }
}
