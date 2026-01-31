package com.atlas_erp.payroll_service.Dto.Response;

import lombok.Data;

@Data
public class SendPayrollToAccountingResponse {

    private String event;
    private Boolean success;
}
