package com.atlas_erp.hr_service.DTO.Response;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class AttendanceSummaryResponse {

    private String period;
    private Integer totalEmployees;
    private Integer totalWorkingDays;
    private BigDecimal totalOvertimeHours;
    private Integer totalAbsentDays;
}
