package com.atlas_erp.reporting_service.Dto.External;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;

@Data
@Builder
public class EmployeeStatisticsResponse {
    private Integer totalEmployees;
    private Integer activeEmployees;
    private Integer newHires;
    private Integer resignations;
    private Double attendanceRate;
    private BigDecimal totalOvertimeHours;
}
