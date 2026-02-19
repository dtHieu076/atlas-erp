package com.atlas_erp.organization_service.Dto.Response.Organization;

import lombok.Data;
import java.util.List;

import com.atlas_erp.organization_service.Dto.OrganizationItemDTO;
import com.atlas_erp.organization_service.Dto.PlantItemDTO;
import com.atlas_erp.organization_service.Dto.DepartmentItemDTO;

// API: GET /organizations/tree
@Data
public class OrganizationTreeResponse {

    private List<OrganizationItemDTO> companies;
    private List<PlantItemDTO> branches;
    private List<DepartmentItemDTO> departments;
}
