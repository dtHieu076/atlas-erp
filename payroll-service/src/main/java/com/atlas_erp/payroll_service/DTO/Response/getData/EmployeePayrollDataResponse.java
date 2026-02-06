package com.atlas_erp.payroll_service.Dto.Response.getData;

import lombok.Data;
import java.util.UUID;
import java.math.BigDecimal;

@Data
public class EmployeePayrollDataResponse {
    private UUID employeeId;
    private String fullName;
    private BigDecimal baseSalary;
    private Double actualWorkedDays;
    private Double standardDays; // Ví dụ: 26 ngày
    private BigDecimal allowances; // Các phụ cấp xưởng gỗ
}
