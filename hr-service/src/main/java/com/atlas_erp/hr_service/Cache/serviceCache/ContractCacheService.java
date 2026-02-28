package com.atlas_erp.hr_service.Cache.serviceCache;

import com.atlas_erp.hr_service.Dto.Response.Contract.EmployeeContractListResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ContractCacheService {

    private final RedisCacheService redisCacheService;
    private static final long TTL_MINUTES = 60;
    private static final String CACHE_KEY_PREFIX = "hr:contract:list:emp:";

    public String generateKey(UUID employeeId) {
        return CACHE_KEY_PREFIX + employeeId.toString();
    }

    public EmployeeContractListResponse getContractList(UUID employeeId) {
        return redisCacheService.get(generateKey(employeeId), EmployeeContractListResponse.class);
    }

    public void putContractList(UUID employeeId, EmployeeContractListResponse data) {
        redisCacheService.put(generateKey(employeeId), data, TTL_MINUTES);
    }

    public void evictContractList(UUID employeeId) {
        redisCacheService.delete(generateKey(employeeId));
    }
}