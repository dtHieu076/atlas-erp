package com.atlas_erp.payroll_service.DTO.Request;

import lombok.Data;

// API: GET /payroll
@Data
public class PayrollListRequest {

    private String periodId;
    private String departmentId;
    private String employeeId;
    private Integer page;
    private Integer pageSize;
}
