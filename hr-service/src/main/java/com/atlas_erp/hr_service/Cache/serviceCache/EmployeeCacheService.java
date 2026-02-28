package com.atlas_erp.hr_service.Cache.serviceCache;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

import com.atlas_erp.hr_service.Dto.Response.Employee.EmployeeProfileResponse;
import com.atlas_erp.hr_service.Dto.Response.Employee.EmployeeResponse;

@Service
@RequiredArgsConstructor
public class EmployeeCacheService {

    private final RedisCacheService redisCacheService;

    // ====== CONSTANT KEY PREFIX ======
    private static final String KEY_ALL = "hr:employee:list:all";
    private static final String KEY_PROFILE = "hr:employee:profile:";

    private static final long TTL_MINUTES = 60;

    /*
     * =========================================================
     * =============== GET ALL EMPLOYEES CACHE =================
     * =========================================================
     */

    public String getAllKey() {
        return KEY_ALL;
    }

    public List<EmployeeResponse> getAllEmployees() {
        return redisCacheService.getList(KEY_ALL, EmployeeResponse.class);
    }

    public void putAllEmployees(List<EmployeeResponse> data) {
        redisCacheService.put(KEY_ALL, data, TTL_MINUTES);
    }

    public void evictAllEmployees() {
        redisCacheService.delete(KEY_ALL);
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
        return redisCacheService.get(getProfileKey(id), EmployeeProfileResponse.class);
    }

    public void putEmployeeProfile(UUID id, EmployeeProfileResponse data) {
        redisCacheService.put(getProfileKey(id), data, TTL_MINUTES);
    }

    public void evictEmployeeProfile(UUID id) {
        redisCacheService.delete(getProfileKey(id));
    }

    /*
     * =========================================================
     * =============== CLEAR ALL EMPLOYEE CACHE =================
     * =========================================================
     */

    public void evictAllEmployeeCaches() {
        redisCacheService.deleteByPattern("hr:employee:*");
    }
}