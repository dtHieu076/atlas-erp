package com.atlas_erp.production_service.Cache.serviceCache;

import com.atlas_erp.production_service.Dto.Request.ProductionOrder.ProductionOrderListRequest;
import com.atlas_erp.production_service.Dto.Response.ProductionOrder.ProductionOrderListResponse;
import com.atlas_erp.production_service.Dto.Response.ProductionPlan.ProductionPlanDetailResponse;
import java.util.UUID;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductionCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration TTL = Duration.ofMinutes(10);
    private static final String PREFIX = "production:order:list:";

    // Tạo key dựa trên hash các tham số filter và phân trang
    private String generateKey(ProductionOrderListRequest request) {
        return PREFIX + String.format("%s-%s-%s-%d-%d",
                request.getPlanId(), request.getStatus(), request.getTeamId(),
                request.getPage(), request.getPageSize());
    }

    public ProductionOrderListResponse getOrderList(ProductionOrderListRequest request) {
        try {
            return (ProductionOrderListResponse) redisTemplate.opsForValue().get(generateKey(request));
        } catch (Exception e) {
            log.error("Redis Get Error: {}", e.getMessage());
            return null;
        }
    }

    public void putOrderList(ProductionOrderListRequest request, ProductionOrderListResponse response) {
        try {
            redisTemplate.opsForValue().set(generateKey(request), response, TTL);
        } catch (Exception e) {
            log.error("Redis Put Error: {}", e.getMessage());
        }
    }

    public void evictAll() {
        var keys = redisTemplate.keys(PREFIX + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }// Bổ sung vào ProductionCacheService.java

    private static final String PLAN_DETAIL_PREFIX = "production:plan:detail:";
    private static final Duration PLAN_TTL = Duration.ofMinutes(15);

    private String generatePlanDetailKey(UUID planId) {
        return PLAN_DETAIL_PREFIX + planId.toString();
    }

    public ProductionPlanDetailResponse getPlanDetail(UUID planId) {
        try {
            return (ProductionPlanDetailResponse) redisTemplate.opsForValue().get(generatePlanDetailKey(planId));
        } catch (Exception e) {
            log.error("Redis Get Plan Detail Error: {}", e.getMessage());
            return null;
        }
    }

    public void putPlanDetail(UUID planId, ProductionPlanDetailResponse response) {
        try {
            redisTemplate.opsForValue().set(generatePlanDetailKey(planId), response, PLAN_TTL);
        } catch (Exception e) {
            log.error("Redis Put Plan Detail Error: {}", e.getMessage());
        }
    }

    public void evictPlanDetail(UUID planId) {
        redisTemplate.delete(generatePlanDetailKey(planId));
    }
}