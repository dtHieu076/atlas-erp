package com.atlas_erp.iam_service.Cache.serviceCache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.atlas_erp.iam_service.Cache.dtoCache.CachedUserIdentity;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class UserIdentityCacheService {

    private static final long TTL_SECONDS = 1800; // 30 phút
    private static final String KEY_PATTERN = "iam:identity:%s:%s";

    private final RedisTemplate<String, Object> redisTemplate;

    // =========================
    // Key builder (private)
    // =========================
    private String buildKey(String provider, String providerUid) {
        return String.format(KEY_PATTERN, provider, providerUid);
    }

    // =========================
    // Public APIs
    // =========================
    public CachedUserIdentity get(String provider, String providerUid) {
        return (CachedUserIdentity) redisTemplate
                .opsForValue()
                .get(buildKey(provider, providerUid));
    }

    public void put(CachedUserIdentity identity) {
        redisTemplate
                .opsForValue()
                .set(
                        buildKey(
                                identity.getProvider(),
                                identity.getProviderUid()),
                        identity,
                        TTL_SECONDS,
                        TimeUnit.SECONDS);
    }

    public void evict(String provider, String providerUid) {
        redisTemplate.delete(buildKey(provider, providerUid));
    }

    public void evict(CachedUserIdentity identity) {
        redisTemplate.delete(
                buildKey(
                        identity.getProvider(),
                        identity.getProviderUid()));
    }
}
