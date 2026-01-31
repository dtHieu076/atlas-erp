package com.atlas_erp.production_service.Service.Impl;

import com.atlas_erp.production_service.Dto.Request.ProductionPlan.*;
import com.atlas_erp.production_service.Dto.Response.ProductionPlan.*;
// import com.atlas_erp.production_service.Entity.ProductionPlanEntity;
// import com.atlas_erp.production_service.Repository.ProductionPlanRepository;
import com.atlas_erp.production_service.Service.ProductionPlanService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductionPlanServiceImpl implements ProductionPlanService {

    // private final ProductionPlanRepository productionPlanRepository;

    @Override
    public CreateProductionPlanResponse createProductionPlan(CreateProductionPlanRequest request) {

        return null;
    }

    @Override
    public ApproveProductionPlanResponse approveProductionPlan(UUID planId, ApproveProductionPlanRequest request) {

        return new ApproveProductionPlanResponse(true);
    }

    @Override
    public RejectProductionPlanResponse rejectProductionPlan(
            UUID planId,
            RejectProductionPlanRequest request) {

        return new RejectProductionPlanResponse();
    }

    @Override
    public ProductionPlanDetailResponse getProductionPlanDetail(UUID planId) {

        return new ProductionPlanDetailResponse();
    }
}
