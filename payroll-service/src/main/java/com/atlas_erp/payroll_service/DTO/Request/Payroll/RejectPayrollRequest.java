package com.atlas_erp.payroll_service.Dto.Request.Payroll;

import lombok.Data;

// API: POST /payroll/{id}/reject
@Data
public class RejectPayrollRequest {

    private String reason;
}
