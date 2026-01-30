package com.atlas_erp.organization_service.Dto.Request.Department;

import lombok.Data;

// API: GET /departments
@Data
public class DepartmentListRequest {

    private String plantId;
    private Integer page;
    private Integer pageSize;
}
