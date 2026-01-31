package com.atlas_erp.payroll_service.Dto.Request.Payroll;

import lombok.Data;

// API: POST /payroll/recalculate
@Data
public class PayrollRecalculateRequest {

    private String periodId;
    private String payrollId; // optional
}
