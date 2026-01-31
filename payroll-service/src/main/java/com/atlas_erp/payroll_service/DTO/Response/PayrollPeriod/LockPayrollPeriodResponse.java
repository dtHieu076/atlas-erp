package com.atlas_erp.payroll_service.Dto.Response.PayrollPeriod;

import lombok.Data;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Data
public class LockPayrollPeriodResponse {

    private Boolean success;

    public static LockPayrollPeriodResponse success() {
        return new LockPayrollPeriodResponse(true);
    }

    public static LockPayrollPeriodResponse fail() {
        return new LockPayrollPeriodResponse(false);
    }
}
