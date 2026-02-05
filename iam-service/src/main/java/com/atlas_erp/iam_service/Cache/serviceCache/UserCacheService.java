package com.atlas_erp.iam_service.Cache.serviceCache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import com.atlas_erp.iam_service.Cache.dtoCache.CachedUser;
import com.atlas_erp.iam_service.Dto.Response.User.UserDataScopeResponse;
import com.atlas_erp.iam_service.Dto.Response.User.UserDetailResponse;
import com.atlas_erp.iam_service.Dto.Response.User.UserRolesResponse;

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

    // Bổ sung vào UserCacheService.java
    private String rolesKey(UUID userId) {
        return "iam:user:roles:" + userId;
    }

    private String scopeKey(UUID userId) {
        return "iam:user:scopes:" + userId;
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

    public UserRolesResponse getUserRoles(UUID userId) {
        return (UserRolesResponse) redisTemplate.opsForValue().get(rolesKey(userId));
    }

    public void putUserRoles(UUID userId, UserRolesResponse roles) {
        redisTemplate.opsForValue().set(rolesKey(userId), roles, TTL);
    }

    public void evictRoles(UUID userId) {
        redisTemplate.delete(rolesKey(userId));
    }

    public UserDetailResponse getUserDetailCache(UUID userId) {
        return (UserDetailResponse) redisTemplate.opsForValue().get("iam:user:detail:" + userId);
    }

    public void putUserDetailCache(UUID userId, UserDetailResponse detail) {
        redisTemplate.opsForValue().set("iam:user:detail:" + userId, detail, TTL);
    }

    public void evictUserDetail(UUID userId) {
        redisTemplate.delete("iam:user:detail:" + userId);
    }

    public UserDataScopeResponse getUserDataScopes(UUID userId) {
        try {
            return (UserDataScopeResponse) redisTemplate.opsForValue().get(scopeKey(userId));
        } catch (Exception e) {
            return null; // Nếu Redis lỗi, trả về null để luồng chính xuống DB
        }
    }

    public void putUserDataScopes(UUID userId, UserDataScopeResponse response) {
        redisTemplate.opsForValue().set(scopeKey(userId), response, Duration.ofMinutes(30));
    }

    public void evictDataScopes(UUID userId) {
        redisTemplate.delete(scopeKey(userId));
    }
}