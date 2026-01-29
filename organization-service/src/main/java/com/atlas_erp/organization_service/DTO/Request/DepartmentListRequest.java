package com.atlas_erp.organization_service.DTO.Request;

import lombok.Data;

// API: GET /departments
@Data
public class DepartmentListRequest {

    private String plantId;
    private Integer page;
    private Integer pageSize;
}
