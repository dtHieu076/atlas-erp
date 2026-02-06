package com.atlas_erp.hr_service.Cache.serviceCache;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Set;
import java.util.UUID;

import com.atlas_erp.hr_service.Dto.Response.Employee.EmployeeAssignmentHistoryResponse;
import com.atlas_erp.hr_service.Dto.Response.Employee.EmployeeDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Employee.EmployeeListResponse;
import com.atlas_erp.hr_service.Dto.Request.Employee.EmployeeListRequest;

@Service
@RequiredArgsConstructor
public class EmployeeCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    // Hàm tạo key dựa trên tất cả các tiêu chí lọc của Request
    // Mục đích: Mỗi bộ lọc khác nhau phải có một kết quả Cache riêng biệt
    public String generateKey(EmployeeListRequest request) {
        return String.format("emp:list:kw:%s:org:%s:pl:%s:dp:%s:tm:%s:st:%s:p:%d:s:%d",
                request.getKeyword() != null ? request.getKeyword() : "all",
                request.getOrganizationId() != null ? request.getOrganizationId() : "all",
                request.getPlantId() != null ? request.getPlantId() : "all",
                request.getDepartmentId() != null ? request.getDepartmentId() : "all",
                request.getTeamId() != null ? request.getTeamId() : "all",
                request.getStatus() != null ? request.getStatus() : "all",
                request.getPage(),
                request.getPageSize());
    }

    public EmployeeListResponse getEmployeeList(String key) {
        return (EmployeeListResponse) redisTemplate.opsForValue().get(key);
    }

    public void putEmployeeList(String key, EmployeeListResponse data) {
        // Danh sách nhân sự thường thay đổi nhiều, để 15-30p là hợp lý
        redisTemplate.opsForValue().set(key, data, Duration.ofMinutes(15));
    }

    // Khi có bất kỳ nhân viên nào được thêm/sửa/xóa, ta phải xóa sạch cache list
    // vì ta không biết nhân viên đó nằm trong bộ lọc (key) nào
    public void evictAllList() {
        Set<String> keys = redisTemplate.keys("emp:list:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }// Trong EmployeeCacheService.java

    public EmployeeDetailResponse getDetail(UUID id) {
        return (EmployeeDetailResponse) redisTemplate.opsForValue().get("emp:detail:" + id);
    }

    public void putDetail(UUID id, EmployeeDetailResponse data) {
        redisTemplate.opsForValue().set("emp:detail:" + id, data, Duration.ofMinutes(60));
    }// Trong EmployeeCacheService.java

    public EmployeeAssignmentHistoryResponse getAssignmentHistory(UUID employeeId) {
        String key = "emp:assignments:" + employeeId;
        return (EmployeeAssignmentHistoryResponse) redisTemplate.opsForValue().get(key);
    }

    public void putAssignmentHistory(UUID employeeId, EmployeeAssignmentHistoryResponse data) {
        String key = "emp:assignments:" + employeeId;
        // Lịch sử ít thay đổi, có thể để TTL dài hơn (ví dụ: 120 phút)
        redisTemplate.opsForValue().set(key, data, Duration.ofMinutes(120));
    }

    public void evictAssignmentHistory(UUID employeeId) {
        redisTemplate.delete("emp:assignments:" + employeeId);
    }

    public void evictDetail(UUID id) {
        redisTemplate.delete("emp:detail:" + id);
    }
}