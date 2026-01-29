package com.atlas_erp.payroll_service.DTO.Request;

import lombok.Data;

// API: POST /payroll/calculate
@Data
public class PayrollCalculateRequest {

    private String periodId;
}
