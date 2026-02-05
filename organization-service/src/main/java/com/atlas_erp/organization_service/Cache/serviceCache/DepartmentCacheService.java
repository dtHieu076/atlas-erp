package com.atlas_erp.organization_service.Cache.serviceCache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.time.Duration;

import com.atlas_erp.organization_service.Dto.Response.Department.DepartmentDetailResponse;
import com.atlas_erp.organization_service.Dto.Response.Department.DepartmentListResponse;

@Service
@RequiredArgsConstructor
public class DepartmentCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public DepartmentListResponse getDeptList(UUID plantId, int page, int size) {
        String key = "dept:list:plant:" + plantId + ":p" + page + ":s" + size;
        return (DepartmentListResponse) redisTemplate.opsForValue().get(key);
    }

    public void putDeptList(UUID plantId, int page, int size, DepartmentListResponse data) {
        String key = "dept:list:plant:" + plantId + ":p" + page + ":s" + size;
        redisTemplate.opsForValue().set(key, data, Duration.ofMinutes(30));
    }

    public void evictDeptListByPlant(UUID plantId) {
        var keys = redisTemplate.keys("dept:list:plant:" + plantId + ":*");
        if (keys != null && !keys.isEmpty())
            redisTemplate.delete(keys);
    }

    public DepartmentDetailResponse getDeptDetail(UUID id) {
        return (DepartmentDetailResponse) redisTemplate.opsForValue().get("dept:detail:" + id);
    }

    public void putDeptDetail(UUID id, DepartmentDetailResponse data) {
        redisTemplate.opsForValue().set("dept:detail:" + id, data, Duration.ofMinutes(60));
    }

    public void evictDeptDetail(UUID id) {
        redisTemplate.delete("dept:detail:" + id);
    }
}
