package com.atlas_erp.production_service.DTO.Request;

import lombok.Data;

@Data
public class CreateDefectRequest {

    private String executionId;
    private String productId;
    private String defectType;
    private Integer quantity;
    private String description;
}
