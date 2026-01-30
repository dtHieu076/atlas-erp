package com.atlas_erp.organization_service.Dto.Response.Department;

import lombok.Data;
import java.time.LocalDateTime;

// API: GET /departments/{id}
@Data
public class DepartmentDetailResponse {

    private String departmentId;
    private String departmentCode;
    private String departmentName;
    private String organizationId;
    private String managerId;
    private String status;
    private LocalDateTime createdAt;
}
