package com.atlas_erp.payroll_service.DTO.Request;

import lombok.Data;

// API: POST /payroll/{id}/approve
@Data
public class ApprovePayrollRequest {

    private String comment;
}
