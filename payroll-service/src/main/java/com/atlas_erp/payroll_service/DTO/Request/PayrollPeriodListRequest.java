package com.atlas_erp.payroll_service.DTO.Request;

import lombok.Data;

// API: GET /periods
@Data
public class PayrollPeriodListRequest {

    private Integer page;
    private Integer pageSize;
}
