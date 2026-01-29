package com.atlas_erp.payroll_service.DTO.Response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// API: GET /payroll/{id}
@Data
public class PayrollDetailResponse {

    private String payrollId;
    private String employeeId;
    private String periodId;
    private BigDecimal baseSalary;
    private BigDecimal overtimePay;
    private BigDecimal bonus;
    private BigDecimal deductions;
    private BigDecimal netSalary;
    private String currency;
    private String status;
    private LocalDateTime generatedAt;
    private LocalDateTime lastUpdatedAt;
}
