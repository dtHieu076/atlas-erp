package com.atlas_erp.organization_service.Dto;

import lombok.Data;

@Data
public class PlantItemDTO {

    private String id;
    private String code;
    private String name;
    private String organizationId;
    private String address;
    private String status;
}
