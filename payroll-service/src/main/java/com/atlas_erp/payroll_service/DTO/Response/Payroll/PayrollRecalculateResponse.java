package com.atlas_erp.payroll_service.Dto.Response.Payroll;

import lombok.Data;

@Data
public class PayrollRecalculateResponse {

    private String result;
    private Integer recalculatedCount;
    private String periodId;
    private String message;
}
