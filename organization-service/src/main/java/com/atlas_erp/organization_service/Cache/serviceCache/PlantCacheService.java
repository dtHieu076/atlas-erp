package com.atlas_erp.organization_service.Cache.serviceCache;

import com.atlas_erp.organization_service.Dto.Response.Plant.PlantListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor // Tự động tạo Constructor để Inject RedisTemplate
public class PlantCacheService {

    private final RedisTemplate<String, Object> redisTemplate;

    // Thời gian sống của Cache (TTL)
    private static final Duration TTL = Duration.ofMinutes(30);

    private String getPlantListKey(UUID orgId, int page, int size) {
        return "plant:list:org:" + orgId + ":p" + page + ":s" + size;
    }

    public PlantListResponse getPlantList(UUID orgId, int page, int size) {
        try {
            return (PlantListResponse) redisTemplate.opsForValue().get(getPlantListKey(orgId, page, size));
        } catch (Exception e) {
            // Trả về null để hệ thống xuống DB nếu Redis có vấn đề
            return null;
        }
    }

    public void putPlantList(UUID orgId, int page, int size, PlantListResponse data) {
        redisTemplate.opsForValue().set(getPlantListKey(orgId, page, size), data, TTL);
    }

    /**
     * Xóa cache danh sách nhà máy của một công ty cụ thể
     * Thường gọi khi Thêm/Sửa/Xóa một Plant thuộc Org đó
     */
    public void evictPlantListByOrg(UUID orgId) {
        var keys = redisTemplate.keys("plant:list:org:" + orgId + ":*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}