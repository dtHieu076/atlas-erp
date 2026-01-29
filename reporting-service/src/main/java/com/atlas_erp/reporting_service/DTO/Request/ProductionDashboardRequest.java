package com.atlas_erp.reporting_service.DTO.Request;

import lombok.Data;

@Data
public class ProductionDashboardRequest {

    private String periodType;
    private String periodValue;
    private String plantId;
}
