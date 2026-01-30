package com.atlas_erp.hr_service.Dto.Request.Employee;

import lombok.Data;

// API: GET /employees
@Data
public class EmployeeListRequest {

    private String keyword;
    private String organizationId;
    private String plantId;
    private String departmentId;
    private String teamId;
    private String status;
    private Integer page;
    private Integer pageSize;
}
