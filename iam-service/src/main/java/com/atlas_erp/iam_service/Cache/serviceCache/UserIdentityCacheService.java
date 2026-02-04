package com.atlas_erp.iam_service.Cache.serviceCache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.atlas_erp.iam_service.Cache.dtoCache.CachedUserIdentity;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class UserIdentityCacheService {

    private static final Duration TTL = Duration.ofMinutes(30);
    private final RedisTemplate<String, Object> redisTemplate;

    private String key(String provider, String providerUid) {
        return "iam:identity:" + provider + ":" + providerUid;
    }

    public CachedUserIdentity get(String provider, String providerUid) {
        return (CachedUserIdentity) redisTemplate
                .opsForValue()
                .get(key(provider, providerUid));
    }

    public void put(CachedUserIdentity identity) {
        redisTemplate.opsForValue().set(
                key(identity.getProvider(), identity.getProviderUid()),
                identity,
                TTL);
    }

    public void evict(String provider, String providerUid) {
        redisTemplate.delete(key(provider, providerUid));
    }

    public void evict(CachedUserIdentity identity) {
        redisTemplate.delete(
                key(identity.getProvider(), identity.getProviderUid()));
    }
}
