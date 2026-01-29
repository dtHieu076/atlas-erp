package com.atlas_erp.production_service.DTO.Request;

import lombok.Data;

@Data
public class ProductionOrderListRequest {

    private String status;
    private String planId;
    private String teamId;
    private Integer page;
    private Integer pageSize;
}
