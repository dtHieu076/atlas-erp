package com.atlas_erp.payroll_service.Dto.Request.Payroll;

import lombok.Data;

// API: POST /payroll/calculate
@Data
public class PayrollCalculateRequest {

    private String periodId;
}
