package com.atlas_erp.reporting_service.Dto.External;

import lombok.Data;

@Data
public class AttendanceSummaryResponse {
    private String period;
    private Integer totalEmployees;
    private Integer totalWorkingDays;
    private Double totalOvertimeHours;
}