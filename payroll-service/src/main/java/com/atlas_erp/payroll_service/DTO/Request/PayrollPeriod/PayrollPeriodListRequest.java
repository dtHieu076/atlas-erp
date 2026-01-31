package com.atlas_erp.payroll_service.Dto.Request.PayrollPeriod;

import lombok.Data;

// API: GET /periods
@Data
public class PayrollPeriodListRequest {

    private Integer page;
    private Integer pageSize;
}
