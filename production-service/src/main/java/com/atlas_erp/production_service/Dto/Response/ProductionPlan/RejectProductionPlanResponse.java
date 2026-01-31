package com.atlas_erp.production_service.Dto.Response.ProductionPlan;

import lombok.Data;
import java.util.Date;

@Data
public class RejectProductionPlanResponse {

    private Boolean success;
    private String productionPlanId;
    private String status;
    private Date rejectedAt;
    private String rejectedBy;
}
