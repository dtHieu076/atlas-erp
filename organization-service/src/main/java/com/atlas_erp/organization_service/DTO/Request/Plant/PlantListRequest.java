package com.atlas_erp.organization_service.Dto.Request.Plant;

import lombok.Data;

// API: GET /plants
@Data
public class PlantListRequest {

    private String organizationId;
    private Integer page;
    private Integer pageSize;
}
