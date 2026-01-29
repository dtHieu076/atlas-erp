package com.atlas_erp.organization_service.DTO.Request;

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
