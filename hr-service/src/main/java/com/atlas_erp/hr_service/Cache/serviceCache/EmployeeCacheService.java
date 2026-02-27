package com.atlas_erp.hr_service.Cache.serviceCache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.atlas_erp.hr_service.Dto.Response.Employee.EmployeeProfileResponse;
import com.atlas_erp.hr_service.Dto.Response.Employee.EmployeeResponse;

@Service
@RequiredArgsConstructor
public class EmployeeCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private final ObjectMapper objectMapper;

    // ====== CONSTANT KEY PREFIX ======
    private static final String KEY_ALL = "emp:all";
    private static final String KEY_PROFILE = "emp:profile:";

    // ====== TTL 60 phút ======
    private static final Duration DURATION = Duration.ofMinutes(60);

    /*
     * =========================================================
     * =============== GET ALL EMPLOYEES CACHE =================
     * =========================================================
     */

    public String getAllKey() {
        return KEY_ALL;
    }

    public List<EmployeeResponse> getAllEmployees() {

        Object cached = redisTemplate.opsForValue().get(KEY_ALL);

        if (cached == null) {
            return null;
        }

        // Nếu deserialize đúng type rồi
        if (cached instanceof List<?> list &&
                !list.isEmpty() &&
                list.get(0) instanceof EmployeeResponse) {
            return (List<EmployeeResponse>) cached;
        }

        // Nếu bị LinkedHashMap do generic erasure
        return objectMapper.convertValue(
                cached,
                new TypeReference<List<EmployeeResponse>>() {
                });
    }

    public void putAllEmployees(List<EmployeeResponse> data) {
        redisTemplate.opsForValue().set(KEY_ALL, data, DURATION);
    }

    public void evictAllEmployees() {
        redisTemplate.delete(KEY_ALL);
    }

    /*
     * =========================================================
     * =============== GET EMPLOYEE PROFILE CACHE ==============
     * =========================================================
     */

    public String getProfileKey(UUID id) {
        return KEY_PROFILE + id;
    }

    public EmployeeProfileResponse getEmployeeProfile(UUID id) {

        Object cached = redisTemplate.opsForValue().get(getProfileKey(id));

        if (cached == null) {
            return null;
        }

        if (cached instanceof EmployeeProfileResponse profile) {
            return profile;
        }

        return objectMapper.convertValue(
                cached,
                EmployeeProfileResponse.class);
    }

    public void putEmployeeProfile(UUID id, EmployeeProfileResponse data) {
        redisTemplate.opsForValue().set(getProfileKey(id), data, DURATION);
    }

    public void evictEmployeeProfile(UUID id) {
        redisTemplate.delete(getProfileKey(id));
    }

    /*
     * =========================================================
     * =============== CLEAR ALL EMPLOYEE CACHE =================
     * =========================================================
     */

    public void evictAllEmployeeCaches() {
        Set<String> keys = redisTemplate.keys("emp:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}