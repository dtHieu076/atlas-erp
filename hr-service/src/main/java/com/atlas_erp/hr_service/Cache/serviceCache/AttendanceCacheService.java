package com.atlas_erp.hr_service.Cache.serviceCache;

import com.atlas_erp.hr_service.Dto.Response.Attendance.AttendanceListResponse;
import com.atlas_erp.hr_service.Dto.Response.Attendance.AttendanceSummaryResponse;
import com.atlas_erp.hr_service.Dto.Request.Attendance.AttendanceListRequest;
import com.atlas_erp.hr_service.Dto.Request.Attendance.AttendanceSummaryRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AttendanceCacheService {
    private final RedisTemplate<String, Object> redisTemplate;

    public String generateKey(AttendanceListRequest request) {
        return String.format("att:list:f:%s:t:%s:e:%s:d:%s",
                request.getFromDate(),
                request.getToDate(),
                request.getEmployeeId() != null ? request.getEmployeeId() : "all",
                request.getDepartmentId() != null ? request.getDepartmentId() : "all");
    }

    public AttendanceListResponse getAttendanceList(String key) {
        return (AttendanceListResponse) redisTemplate.opsForValue().get(key);
    }

    public void putAttendanceList(String key, AttendanceListResponse data) {
        // Chấm công thường được xem nhiều nhưng ít thay đổi trong ngày (trừ khi có điều
        // chỉnh thủ công)
        // Lưu cache trong 30 phút là hợp lý
        redisTemplate.opsForValue().set(key, data, Duration.ofMinutes(30));
    }

    public void evictAttendanceCache() {
        var keys = redisTemplate.keys("att:list:*");
        if (keys != null && !keys.isEmpty())
            redisTemplate.delete(keys);
    }

    public String generateSummaryKey(AttendanceSummaryRequest request) {
        return String.format("att:summary:p:%s:d:%s:t:%s",
                request.getPeriod(),
                request.getDepartmentId() != null ? request.getDepartmentId() : "all",
                request.getTeamId() != null ? request.getTeamId() : "all");
    }

    public AttendanceSummaryResponse getSummary(String key) {
        return (AttendanceSummaryResponse) redisTemplate.opsForValue().get(key);
    }

    public void putSummary(String key, AttendanceSummaryResponse data) {
        // Báo cáo tháng thường ít thay đổi liên tục, có thể lưu 1 tiếng (60p)
        redisTemplate.opsForValue().set(key, data, Duration.ofMinutes(60));
    }

    public void evictAllAttendanceCache() {
        // Xóa cả list chi tiết và các báo cáo tổng hợp
        Set<String> keys = redisTemplate.keys("att:*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }
}