package com.atlas_erp.hr_service.Cache.serviceCache;

import com.atlas_erp.hr_service.Dto.Response.Contract.EmployeeContractListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final String CACHE_KEY_PREFIX = "contract:list:emp:";

    public String generateKey(UUID employeeId) {
        return CACHE_KEY_PREFIX + employeeId.toString();
    }

    public EmployeeContractListResponse getContractList(UUID employeeId) {
        return (EmployeeContractListResponse) redisTemplate.opsForValue().get(generateKey(employeeId));
    }

    public void putContractList(UUID employeeId, EmployeeContractListResponse data) {
        // Hợp đồng ít thay đổi, có thể lưu cache trong 60 phút
        redisTemplate.opsForValue().set(generateKey(employeeId), data, Duration.ofMinutes(60));
    }

    public void evictContractList(UUID employeeId) {
        redisTemplate.delete(generateKey(employeeId));
    }
}