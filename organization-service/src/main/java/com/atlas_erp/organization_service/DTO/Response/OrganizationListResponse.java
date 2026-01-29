package com.atlas_erp.organization_service.DTO.Response;

import com.atlas_erp.organization_service.DTO.OrganizationItemDTO;
import lombok.Data;
import java.util.List;

// API: GET /organizations
@Data
public class OrganizationListResponse {

    private List<OrganizationItemDTO> list;
    private Long total;
}
