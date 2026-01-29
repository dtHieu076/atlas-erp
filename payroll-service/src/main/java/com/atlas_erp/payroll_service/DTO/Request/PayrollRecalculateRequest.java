package com.atlas_erp.payroll_service.DTO.Request;

import lombok.Data;

// API: POST /payroll/recalculate
@Data
public class PayrollRecalculateRequest {

    private String periodId;
    private String payrollId; // optional
}
