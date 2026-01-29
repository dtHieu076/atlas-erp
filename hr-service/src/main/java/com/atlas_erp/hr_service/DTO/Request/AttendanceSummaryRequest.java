package com.atlas_erp.hr_service.DTO.Request;

import lombok.Data;

// API: GET /attendance/summary
@Data
public class AttendanceSummaryRequest {

    private String period;
    private String departmentId;
    private String teamId;
}
