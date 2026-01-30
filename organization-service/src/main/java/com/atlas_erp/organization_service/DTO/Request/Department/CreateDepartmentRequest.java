package com.atlas_erp.organization_service.Dto.Request.Department;

import lombok.Data;

// API: POST /departments
@Data
public class CreateDepartmentRequest {

    private String code;
    private String name;
    private String plantId;
    private String status;
}
