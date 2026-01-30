package com.atlas_erp.organization_service.Service;

import com.atlas_erp.organization_service.Dto.Request.Department.*;
import com.atlas_erp.organization_service.Dto.Response.Department.*;

import java.util.UUID;

public interface DepartmentService {

    DepartmentListResponse getDepartments(DepartmentListRequest request);

    CreateDepartmentResponse createDepartment(CreateDepartmentRequest request);

    DepartmentDetailResponse getDepartmentDetail(UUID id);
}
