package com.atlas_erp.payroll_service.DTO;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class PayrollDetailItemDTO {

    private String employeeId;
    private String employeeCode;
    private String fullName;
    private BigDecimal baseSalary;
    private BigDecimal overtimePay;
    private BigDecimal allowance;
    private BigDecimal deduction;
    private BigDecimal netSalary;
    private String status;
}
