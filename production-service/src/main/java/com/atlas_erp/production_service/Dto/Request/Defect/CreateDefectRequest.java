package com.atlas_erp.production_service.Dto.Request.Defect;

import lombok.Data;

@Data
public class CreateDefectRequest {

    private String executionId;
    private String productId;
    private String defectType;
    private Integer quantity;
    private String description;
}
