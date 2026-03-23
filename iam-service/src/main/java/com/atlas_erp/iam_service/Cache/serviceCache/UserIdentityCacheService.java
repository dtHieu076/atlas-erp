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

    private String key(String googleId) {
        return "iam:identity:google:" + googleId;
    }

    private String keyByUserId(UUID userId) {
        return "iam:identity:user:" + userId;
    }

    // ========== GET ==========

    /**
     * @deprecated Use get(String googleId)
     */
    @Deprecated
    public CachedUserIdentity get(String provider, String providerUid) {
        return get(providerUid); // Assume providerUid is googleId
    }

    public CachedUserIdentity get(String googleId) {
        return (CachedUserIdentity) redisTemplate
                .opsForValue()
                .get(key(googleId));
    }

    public CachedUserIdentity getByUserId(UUID userId) {
        return (CachedUserIdentity) redisTemplate
                .opsForValue()
                .get(keyByUserId(userId));
    }

    // ========== PUT ==========

    public void put(CachedUserIdentity identity) {
        // by googleId (primary key)
        redisTemplate.opsForValue().set(
                key(identity.getGoogleId()),
                identity,
                TTL);

        // by userId (for refresh flow)
        redisTemplate.opsForValue().set(
                keyByUserId(identity.getUserId()),
                identity,
                TTL);
    }

    // ========== EVICT ==========

    public void evict(CachedUserIdentity identity) {
        redisTemplate.delete(key(identity.getGoogleId()));
        redisTemplate.delete(keyByUserId(identity.getUserId()));
    }

    /**
     * @deprecated Use evict(CachedUserIdentity)
     */
    @Deprecated
    public void evict(String provider, String providerUid) {
        evict(providerUid); // Assume providerUid is googleId
    }

    public void evict(String googleId) {
        redisTemplate.delete(key(googleId));
    }

    public void evictByUserId(UUID userId) {
        redisTemplate.delete(keyByUserId(userId));
    }
}
