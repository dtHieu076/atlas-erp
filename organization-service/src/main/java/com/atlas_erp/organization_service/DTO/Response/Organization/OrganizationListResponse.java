package com.atlas_erp.organization_service.Dto.Response.Organization;

import lombok.Data;
import java.util.List;

import com.atlas_erp.organization_service.Dto.OrganizationItemDTO;

// API: GET /organizations
@Data
public class OrganizationListResponse {

    private List<OrganizationItemDTO> list;
    private Long total;
}
