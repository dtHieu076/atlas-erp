package com.atlas_erp.payroll_service.Cache.serviceCache;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.atlas_erp.payroll_service.Dto.Request.Payroll.PayrollListRequest;
import com.atlas_erp.payroll_service.Dto.Response.Payroll.PayrollListResponse;
import com.atlas_erp.payroll_service.Dto.Response.PayrollPeriod.PayrollPeriodListResponse;
import com.atlas_erp.payroll_service.Entity.PayrollPeriodEntity;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class PayrollPeriodCacheService {

    // Thời gian sống của cache kỳ lương (thường để lâu hơn user, ví dụ: 60 phút)
    private static final Duration TTL = Duration.ofHours(1);

    private final RedisTemplate<String, Object> redisTemplate;

    // Định nghĩa tiền tố Key để dùng chung trong Redis
    private String key(UUID periodId) {
        return "payroll:period:" + periodId;
    }

    private String codeKey(String periodCode) {
        return "payroll:period:code:" + periodCode;
    }

    // --- Thao tác theo ID ---

    public PayrollPeriodEntity getPeriod(UUID periodId) {
        try {
            return (PayrollPeriodEntity) redisTemplate.opsForValue().get(key(periodId));
        } catch (Exception e) {
            log.error("Redis error getting period by ID: {}", e.getMessage());
            return null;
        }
    }

    public void putPeriod(PayrollPeriodEntity period) {
        if (period == null || period.getId() == null)
            return;
        redisTemplate.opsForValue().set(key(period.getId()), period, TTL);
        // Đồng bộ luôn cache theo Code để tránh query DB
        if (period.getPeriodCode() != null) {
            redisTemplate.opsForValue().set(codeKey(period.getPeriodCode()), period, TTL);
        }
    }

    public void evict(UUID periodId) {
        redisTemplate.delete(key(periodId));
    }

    // --- Thao tác theo Period Code (Ví dụ: 02-2026) ---

    public PayrollPeriodEntity getPeriodByCode(String periodCode) {
        return (PayrollPeriodEntity) redisTemplate.opsForValue().get(codeKey(periodCode));
    }

    // --- Xóa Cache khi Lock/Unlock ---

    public void evictAll(PayrollPeriodEntity period) {
        if (period == null)
            return;
        redisTemplate.delete(key(period.getId()));
        if (period.getPeriodCode() != null) {
            redisTemplate.delete(codeKey(period.getPeriodCode()));
        }
    }// Trong PayrollPeriodCacheService.java

    private String listKey(int page, int size) {
        return "payroll:period:list:" + page + ":" + size;
    }

    public PayrollPeriodListResponse getListCache(int page, int size) {
        return (PayrollPeriodListResponse) redisTemplate.opsForValue().get(listKey(page, size));
    }

    public void putListCache(int page, int size, PayrollPeriodListResponse response) {
        // Cache danh sách thường để TTL ngắn hơn (ví dụ 10-15 phút) vì dữ liệu thay đổi
        // thường xuyên
        redisTemplate.opsForValue().set(listKey(page, size), response, Duration.ofMinutes(15));
    }

    public void evictAllListCache() {
        // Xóa tất cả các key có prefix payroll:period:list:* khi có kỳ lương mới được
        // tạo hoặc bị xóa
        java.util.Set<String> keys = redisTemplate.keys("payroll:period:list:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }

    public String payrollListKey(PayrollListRequest request) {
        return String.format("payroll:detail:list:%s:%s:%s:%d:%d",
                request.getPeriodId(),
                request.getDepartmentId() != null ? request.getDepartmentId() : "all",
                request.getEmployeeId() != null ? request.getEmployeeId() : "all",
                request.getPage(),
                request.getPageSize());
    }

    public PayrollListResponse getPayrollListCache(PayrollListRequest request) {
        return (PayrollListResponse) redisTemplate.opsForValue().get(payrollListKey(request));
    }

    public void putPayrollListCache(PayrollListRequest request, PayrollListResponse response) {
        redisTemplate.opsForValue().set(payrollListKey(request), response, Duration.ofMinutes(10));
    }
}