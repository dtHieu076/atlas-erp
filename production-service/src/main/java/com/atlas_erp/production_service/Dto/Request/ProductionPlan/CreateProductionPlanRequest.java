package com.atlas_erp.production_service.Dto.Request.ProductionPlan;

import lombok.Data;
import java.util.Date;

@Data
public class CreateProductionPlanRequest {

    private String planCode;
    private String productId;
    private Integer quantity;
    private Date startDate;
    private Date endDate;
    private String plantId;
    private String departmentId;
    private String note;
}
