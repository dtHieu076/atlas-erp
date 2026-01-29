package com.atlas_erp.payroll_service.DTO.Request;

import lombok.Data;

// API: POST /periods/{id}/lock
@Data
public class LockPayrollPeriodRequest {

    private String periodId;
}
