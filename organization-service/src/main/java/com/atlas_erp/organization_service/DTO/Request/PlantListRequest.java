package com.atlas_erp.organization_service.DTO.Request;

import lombok.Data;

// API: GET /plants
@Data
public class PlantListRequest {

    private String organizationId;
    private Integer page;
    private Integer pageSize;
}
