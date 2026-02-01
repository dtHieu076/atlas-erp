package com.atlas_erp.reporting_service.Dto.Response;

import lombok.Data;

@Data
public class HRDashboardResponse {

    private String period;
    private Integer totalEmployees;
    private Integer activeEmployees;
    private Integer newHires;
    private Integer resignations;
    private Double attendanceRate;
    private Double overtimeHours;
}
