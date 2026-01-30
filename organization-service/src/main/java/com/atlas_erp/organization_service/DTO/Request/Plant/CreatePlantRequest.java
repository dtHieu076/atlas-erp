package com.atlas_erp.organization_service.Dto.Request.Plant;

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
