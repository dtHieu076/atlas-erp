package com.atlas_erp.production_service.Service;

import com.atlas_erp.production_service.Dto.Request.ProductionPlan.*;
import com.atlas_erp.production_service.Dto.Response.ProductionPlan.*;
import java.util.UUID;

public interface ProductionPlanService {

        CreateProductionPlanResponse createProductionPlan(CreateProductionPlanRequest request);

        ApproveProductionPlanResponse approveProductionPlan(UUID planId, ApproveProductionPlanRequest request);

        RejectProductionPlanResponse rejectProductionPlan(UUID planId, RejectProductionPlanRequest request);

        ProductionPlanDetailResponse getProductionPlanDetail(UUID planId);
}
