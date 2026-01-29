package com.atlas_erp.organization_service.DTO.Request;

import lombok.Data;

// API: POST /plants
@Data
public class CreatePlantRequest {

    private String code;
    private String name;
    private String organizationId;
    private String address;
    private String status;
}
