package com.atlas_erp.organization_service.Dto.Response.Department;

import lombok.Data;
import java.util.List;

import com.atlas_erp.organization_service.Dto.DepartmentItemDTO;

@Data
public class DepartmentListResponse {

    private List<DepartmentItemDTO> list;
    private Long total;
}
