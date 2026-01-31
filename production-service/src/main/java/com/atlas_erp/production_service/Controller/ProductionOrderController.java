package com.atlas_erp.production_service.Controller;

import com.atlas_erp.production_service.Dto.Request.ProductionOrder.*;
import com.atlas_erp.production_service.Dto.Response.ProductionOrder.*;
import com.atlas_erp.production_service.Service.ProductionOrderService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/production/production-orders")
@RequiredArgsConstructor
public class ProductionOrderController {

    private final ProductionOrderService productionOrderService;

    // 3️⃣ POST /production-orders
    @PostMapping
    public ResponseEntity<CreateProductionOrderResponse> createProductionOrder(
            @RequestBody CreateProductionOrderRequest request) {
        return ResponseEntity.ok(productionOrderService.createProductionOrder(request));
    }

    // 4️⃣ GET /production-orders
    @GetMapping
    public ResponseEntity<ProductionOrderListResponse> getProductionOrders(
            ProductionOrderListRequest request) {
        return ResponseEntity.ok(productionOrderService.getProductionOrders(request));
    }
}
