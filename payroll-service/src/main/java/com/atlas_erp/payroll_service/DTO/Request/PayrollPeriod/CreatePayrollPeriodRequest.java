package com.atlas_erp.payroll_service.Dto.Request.PayrollPeriod;

import lombok.Data;
import java.time.LocalDate;

// API: POST /periods
@Data
public class CreatePayrollPeriodRequest {

    private Integer month;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
}
