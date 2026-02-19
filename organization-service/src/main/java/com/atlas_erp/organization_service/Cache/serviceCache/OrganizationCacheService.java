package com.atlas_erp.organization_service.Cache.serviceCache;

import com.atlas_erp.organization_service.Dto.Response.Organization.OrganizationDetailResponse;
import com.atlas_erp.organization_service.Dto.Response.Organization.OrganizationListResponse;
import com.atlas_erp.organization_service.Dto.Response.Organization.OrganizationTreeResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.time.Duration;

@Service
@RequiredArgsConstructor
public class OrganizationCacheService {

    private static final Duration TTL = Duration.ofMinutes(60); // ERP Organization ít thay đổi nên để 60p
    private final RedisTemplate<String, Object> redisTemplate;

    private String getListKey(int page, int size) {
        return "org:list:p" + page + ":s" + size;
    }

    public OrganizationListResponse getOrganizationList(int page, int size) {
        try {
            return (OrganizationListResponse) redisTemplate.opsForValue().get(getListKey(page, size));
        } catch (Exception e) {
            return null;
        }
    }

    public void putOrganizationList(int page, int size, OrganizationListResponse data) {
        redisTemplate.opsForValue().set(getListKey(page, size), data, TTL);
    }

    /**
     * Xóa toàn bộ cache liên quan đến danh sách khi có thay đổi (Thêm/Sửa/Xóa)
     */
    public void evictAllListCache() {
        var keys = redisTemplate.keys("org:list:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    // --- Logic cho Cache Chi tiết (Bổ sung tại đây) ---
    private String getDetailKey(UUID id) {
        return "org:detail:" + id;
    }

    public void evictDetail(UUID id) {
        // Xóa chính xác key của công ty vừa được cập nhật
        redisTemplate.delete(getDetailKey(id));
    }

    // Hàm này dùng cho API GET /organizations/{id}
    public void putDetail(UUID id, Object data) {
        redisTemplate.opsForValue().set(getDetailKey(id), data, TTL);
    }

    // Trong file OrganizationCacheService.java

    public OrganizationDetailResponse getDetail(UUID id) {
        try {
            String key = "org:detail:" + id;
            return (OrganizationDetailResponse) redisTemplate.opsForValue().get(key);
        } catch (Exception e) {
            // Nếu lỗi Redis, log lại và trả về null để luồng chính tiếp tục xuống DB
            return null;
        }
    }

    public void putDetail(UUID id, OrganizationDetailResponse data) {
        String key = "org:detail:" + id;
        redisTemplate.opsForValue().set(key, data, Duration.ofMinutes(60));
    }

    // =====================================================
    // TREE CACHE
    // =====================================================

    private static final String TREE_KEY = "org:tree";

    public OrganizationTreeResponse getTree() {
        try {
            return (OrganizationTreeResponse) redisTemplate.opsForValue().get(TREE_KEY);
        } catch (Exception e) {
            return null;
        }
    }

    public void putTree(OrganizationTreeResponse data) {
        redisTemplate.opsForValue().set(TREE_KEY, data, TTL);
    }

    public void evictTree() {
        redisTemplate.delete(TREE_KEY);
    }

}