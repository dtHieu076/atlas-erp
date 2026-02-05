package com.atlas_erp.organization_service.Cache.serviceCache;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import lombok.RequiredArgsConstructor;
import java.time.Duration;
import java.util.UUID;

import com.atlas_erp.organization_service.Dto.Response.Team.TeamDetailResponse;
import com.atlas_erp.organization_service.Dto.Response.Team.TeamListResponse;

@Service
@RequiredArgsConstructor
public class TeamCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public TeamListResponse getTeamList(UUID deptId, int page, int size) {
        String key = "team:list:dept:" + deptId + ":p" + page + ":s" + size;
        return (TeamListResponse) redisTemplate.opsForValue().get(key);
    }

    public void putTeamList(UUID deptId, int page, int size, TeamListResponse data) {
        String key = "team:list:dept:" + deptId + ":p" + page + ":s" + size;
        redisTemplate.opsForValue().set(key, data, Duration.ofMinutes(30));
    }

    public void evictTeamListByDept(UUID deptId) {
        var keys = redisTemplate.keys("team:list:dept:" + deptId + ":*");
        if (keys != null && !keys.isEmpty())
            redisTemplate.delete(keys);
    }
    // Trong TeamCacheService.java

    public TeamDetailResponse getTeamDetail(UUID id) {
        String key = "team:detail:" + id;
        return (TeamDetailResponse) redisTemplate.opsForValue().get(key);
    }

    public void putTeamDetail(UUID id, TeamDetailResponse data) {
        String key = "team:detail:" + id;
        redisTemplate.opsForValue().set(key, data, Duration.ofMinutes(60));
    }

    public void evictTeamDetail(UUID id) {
        String key = "team:detail:" + id;
        redisTemplate.delete(key);
    }
}
