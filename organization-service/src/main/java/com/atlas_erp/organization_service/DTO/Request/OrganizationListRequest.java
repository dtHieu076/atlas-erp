package com.atlas_erp.organization_service.DTO.Request;

import lombok.Data;

// API: GET /organizations
@Data
public class OrganizationListRequest {

    private Integer page;
    private Integer pageSize;
}
