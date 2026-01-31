package com.atlas_erp.production_service.Controller;

import com.atlas_erp.production_service.Dto.Request.ProductionPlan.*;
import com.atlas_erp.production_service.Dto.Response.ProductionPlan.*;
import com.atlas_erp.production_service.Service.ProductionPlanService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/production/production-plans")
@RequiredArgsConstructor
public class ProductionPlanController {

    private final ProductionPlanService productionPlanService;

    // 1️⃣ POST /production-plans
    @PostMapping
    public CreateProductionPlanResponse createProductionPlan(@RequestBody CreateProductionPlanRequest request) {
        return productionPlanService.createProductionPlan(request);
    }

    // 2️⃣ POST /production-plans/{id}/approve
    @PostMapping("/{id}/approve")
    public ApproveProductionPlanResponse approveProductionPlan(
            @PathVariable UUID id,
            @RequestBody ApproveProductionPlanRequest request) {
        return productionPlanService.approveProductionPlan(id, request);
    }

    // 9️⃣ POST /production-plans/{id}/reject
    @PostMapping("/{id}/reject")
    public RejectProductionPlanResponse rejectProductionPlan(
            @PathVariable UUID id,
            @RequestBody RejectProductionPlanRequest request) {
        return productionPlanService.rejectProductionPlan(id, request);
    }

    // 8️⃣ GET /production-plans/{id}
    @GetMapping("/{id}")
    public ProductionPlanDetailResponse getProductionPlanDetail(
            @PathVariable UUID id) {
        return productionPlanService.getProductionPlanDetail(id);
    }
}
