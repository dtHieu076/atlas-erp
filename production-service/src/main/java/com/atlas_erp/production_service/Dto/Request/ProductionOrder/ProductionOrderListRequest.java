package com.atlas_erp.production_service.Dto.Request.ProductionOrder;

import lombok.Data;

@Data
public class ProductionOrderListRequest {

    private String status;
    private String planId;
    private String teamId;
    private Integer page;
    private Integer pageSize;
}
