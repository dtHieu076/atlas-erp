package com.atlas_erp.organization_service.DTO.Request;

import lombok.Data;

// API: POST /organizations
@Data
public class CreateOrganizationRequest {

    private String code;
    private String name;
    private String address;
    private String phone;
    private String email;
    private String status;
}
