package com.atlas_erp.production_service.DTO.Request;

import lombok.Data;
import java.util.Date;

@Data
public class CreateProductionOrderRequest {

    private String planId;
    private String orderCode;
    private String productId;
    private Integer quantity;
    private String teamId;
    private Date startDate;
    private Date endDate;
}
