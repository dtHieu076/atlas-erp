package com.atlas_erp.reporting_service.Dto.External;

import lombok.Data;

@Data
public class HRSummaryResponse {
    private Integer totalEmployees;
    private Integer activeEmployees;
    private Integer newHires;
    private Integer resignations;
    private Double attendanceRate;
    private Double totalOvertimeHours;
}