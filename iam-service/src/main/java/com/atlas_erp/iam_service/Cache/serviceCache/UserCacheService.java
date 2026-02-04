package com.atlas_erp.iam_service.Cache.serviceCache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.atlas_erp.iam_service.Cache.dtoCache.CachedUser;
import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class UserCacheService {

    private static final Duration TTL = Duration.ofMinutes(30);

    private final RedisTemplate<String, Object> redisTemplate;

    private String key(UUID userId) {
        return "iam:user:" + userId;
    }

    public CachedUser getUser(UUID userId) {
        return (CachedUser) redisTemplate
                .opsForValue()
                .get(key(userId));
    }

    public void putUser(CachedUser user) {
        redisTemplate
                .opsForValue()
                .set(key(user.getUserId()), user, TTL);
    }

    public void evict(UUID userId) {
        redisTemplate.delete(key(userId));
    }
}