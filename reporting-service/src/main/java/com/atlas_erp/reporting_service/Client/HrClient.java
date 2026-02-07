package com.atlas_erp.reporting_service.Client;

import com.atlas_erp.reporting_service.Dto.External.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.cloud.openfeign.SpringQueryMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "hr-service")
public interface HrClient {

    // Gọi API số 15 của HR Service: Lấy tổng hợp chấm công, OT, vắng mặt
    @GetMapping("/attendance/summary")
    AttendanceSummaryResponse getAttendanceSummary(
            @RequestParam("period") String period,
            @RequestParam(value = "departmentId", required = false) String departmentId);

    @GetMapping("/employees/statistics/summary")
    EmployeeStatisticsResponse getEmployeeStatistics(@SpringQueryMap EmployeeStatisticsRequest request);
}