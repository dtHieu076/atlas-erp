package com.atlas_erp.reporting_service.Cache.serviceCache;

import com.atlas_erp.reporting_service.Dto.Response.ExecutiveDashboardResponse;
import com.atlas_erp.reporting_service.Dto.Response.HRDashboardResponse;
import com.atlas_erp.reporting_service.Dto.Response.InventoryDashboardResponse;
import com.atlas_erp.reporting_service.Dto.Response.ProductionDashboardResponse;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
public class ReportCacheService {

    private static final Duration TTL = Duration.ofMinutes(15); // Dashboard nên để 15p
    private final RedisTemplate<String, Object> redisTemplate;

    private String getExecutiveKey(String periodType, String periodValue) {
        return String.format("reporting:dashboard:executive:%s:%s", periodType, periodValue);
    }

    public ExecutiveDashboardResponse getExecutiveDashboard(String type, String value) {
        return (ExecutiveDashboardResponse) redisTemplate.opsForValue().get(getExecutiveKey(type, value));
    }

    public void putExecutiveDashboard(String type, String value, ExecutiveDashboardResponse response) {
        redisTemplate.opsForValue().set(getExecutiveKey(type, value), response, TTL);
    }

    public void evictExecutiveDashboard(String type, String value) {
        redisTemplate.delete(getExecutiveKey(type, value));
    }

    public HRDashboardResponse getHRDashboard(String period, String deptId) {
        String key = "reporting:dashboard:hr:" + period + ":" + (deptId != null ? deptId : "ALL");
        return (HRDashboardResponse) redisTemplate.opsForValue().get(key);
    }

    public void putHRDashboard(HRDashboardResponse response) {
        String key = "reporting:dashboard:hr:" + response.getPeriod() + ":" + "TODO_DEPT_ID";
        redisTemplate.opsForValue().set(key, response, Duration.ofMinutes(10));
    }// Bổ sung vào ReportCacheService.java

    // Bổ sung vào ReportCacheService.java

    public InventoryDashboardResponse getInventoryDashboard(String warehouseId, String period) {
        String key = "reporting:dashboard:inventory:" + warehouseId + ":" + period;
        return (InventoryDashboardResponse) redisTemplate.opsForValue().get(key);
    }

    public void putInventoryDashboard(InventoryDashboardResponse response) {
        // Lưu key theo Warehouse và Period, TTL 15 phút
        String key = "reporting:dashboard:inventory:" + response.getWarehouseId() + ":" + "LATEST";
        redisTemplate.opsForValue().set(key, response, Duration.ofMinutes(15));
    }

    public ProductionDashboardResponse getProductionDashboard(String plantId, String type, String value) {
        String key = String.format("reporting:dashboard:production:%s:%s:%s",
                plantId != null ? plantId : "ALL", type, value);
        return (ProductionDashboardResponse) redisTemplate.opsForValue().get(key);
    }

    public void putProductionDashboard(String plantId, String type, String value,
            ProductionDashboardResponse response) {
        String key = String.format("reporting:dashboard:production:%s:%s:%s",
                plantId != null ? plantId : "ALL", type, value);
        redisTemplate.opsForValue().set(key, response, Duration.ofMinutes(15));
    }
}