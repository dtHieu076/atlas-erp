package com.atlas_erp.hr_service.Cache.serviceCache;

import com.atlas_erp.hr_service.Dto.Response.Attendance.AttendanceListResponse;
import com.atlas_erp.hr_service.Dto.Response.Attendance.AttendanceSummaryResponse;
import com.atlas_erp.hr_service.Dto.Request.Attendance.AttendanceListRequest;
import com.atlas_erp.hr_service.Dto.Request.Attendance.AttendanceSummaryRequest;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class AttendanceCacheService {
    private final RedisCacheService redisCacheService;

    private static final long LIST_TTL = 30;
    private static final long SUMMARY_TTL = 60;

    public String generateKey(AttendanceListRequest request) {
        return String.format("hr:attendance:list:f:%s:t:%s:e:%s:d:%s",
                request.getFromDate(),
                request.getToDate(),
                request.getEmployeeId() != null ? request.getEmployeeId() : "all",
                request.getDepartmentId() != null ? request.getDepartmentId() : "all");
    }

    public AttendanceListResponse getAttendanceList(String key) {
        return redisCacheService.get(key, AttendanceListResponse.class);
    }

    public void putAttendanceList(String key, AttendanceListResponse data) {
        redisCacheService.put(key, data, LIST_TTL);
    }

    public void evictAttendanceCache() {
        redisCacheService.deleteByPattern("hr:attendance:list:*");
    }

    public String generateSummaryKey(AttendanceSummaryRequest request) {
        return String.format("hr:attendance:summary:p:%s:d:%s:t:%s",
                request.getPeriod(),
                request.getDepartmentId() != null ? request.getDepartmentId() : "all",
                request.getTeamId() != null ? request.getTeamId() : "all");
    }

    public AttendanceSummaryResponse getSummary(String key) {
        return redisCacheService.get(key, AttendanceSummaryResponse.class);
    }

    public void putSummary(String key, AttendanceSummaryResponse data) {
        redisCacheService.put(key, data, SUMMARY_TTL);
    }

    public void evictAllAttendanceCache() {
        redisCacheService.deleteByPattern("hr:attendance:*");
    }
}