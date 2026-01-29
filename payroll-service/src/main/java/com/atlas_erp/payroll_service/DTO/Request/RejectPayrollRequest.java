package com.atlas_erp.payroll_service.DTO.Request;

import lombok.Data;

// API: POST /payroll/{id}/reject
@Data
public class RejectPayrollRequest {

    private String reason;
}
