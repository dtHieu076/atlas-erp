package com.atlas_erp.organization_service.Dto.Request.Organization;

import lombok.Data;

// API: GET /organizations
@Data
public class OrganizationListRequest {

    private Integer page;
    private Integer pageSize;
}
