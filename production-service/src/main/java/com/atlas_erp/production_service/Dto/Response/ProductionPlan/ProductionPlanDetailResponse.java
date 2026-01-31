package com.atlas_erp.production_service.Dto.Response.ProductionPlan;

import lombok.Data;
import java.util.Date;

@Data
public class ProductionPlanDetailResponse {

    private String productionPlanId;
    private String planCode;
    private String productId;
    private String productName;
    private Integer quantityPlanned;
    private String unit;
    private Date startDate;
    private Date endDate;
    private String status;
    private Integer progressPercent;
}
