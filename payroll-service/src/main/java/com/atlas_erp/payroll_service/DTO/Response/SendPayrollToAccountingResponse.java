package com.atlas_erp.payroll_service.DTO.Response;

import lombok.Data;

@Data
public class SendPayrollToAccountingResponse {

    private String event;
    private Boolean success;
}
