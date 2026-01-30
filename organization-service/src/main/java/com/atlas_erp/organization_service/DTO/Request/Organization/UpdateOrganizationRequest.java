package com.atlas_erp.organization_service.Dto.Request.Organization;

import lombok.Data;

// API: PUT /organizations/{id}
@Data
public class UpdateOrganizationRequest {

    private String name;
    private String address;
    private String phone;
    private String email;
    private String status;
}
