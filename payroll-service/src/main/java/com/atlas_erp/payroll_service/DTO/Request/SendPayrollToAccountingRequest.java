package com.atlas_erp.payroll_service.Dto.Request;

import lombok.Data;

// API: POST /payroll/send-to-accounting
@Data
public class SendPayrollToAccountingRequest {

    private String periodId;
}
