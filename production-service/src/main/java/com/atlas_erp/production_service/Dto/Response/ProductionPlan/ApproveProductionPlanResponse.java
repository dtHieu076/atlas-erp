package com.atlas_erp.production_service.Dto.Response.ProductionPlan;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class ApproveProductionPlanResponse {
    private Boolean success;

    public ApproveProductionPlanResponse success() {
        return new ApproveProductionPlanResponse(true);
    }

    public ApproveProductionPlanResponse fail() {
        return new ApproveProductionPlanResponse(false);
    }
}
