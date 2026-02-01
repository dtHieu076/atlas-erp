package com.atlas_erp.reporting_service.Controller;

import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.atlas_erp.reporting_service.Service.*;
import com.atlas_erp.reporting_service.Dto.Request.*;
import com.atlas_erp.reporting_service.Dto.Response.*;

@RestController
@RequestMapping("/dashboards")
@RequiredArgsConstructor
public class DashboardController {

    private final ExecutiveDashboardService executiveDashboardService;
    private final ProductionDashboardService productionDashboardService;
    private final HRDashboardService hrDashboardService;
    private final InventoryDashboardService inventoryDashboardService;

    @GetMapping("/executive")
    public ExecutiveDashboardResponse executiveDashboard(
            ExecutiveDashboardRequest request) {
        return executiveDashboardService.getExecutiveDashboard(request);
    }

    @GetMapping("/production")
    public ProductionDashboardResponse productionDashboard(
            ProductionDashboardRequest request) {
        return productionDashboardService.getProductionDashboard(request);
    }

    @GetMapping("/hr")
    public HRDashboardResponse hrDashboard(
            HRDashboardRequest request) {
        return hrDashboardService.getHRDashboard(request);
    }

    @GetMapping("/inventory")
    public InventoryDashboardResponse inventoryDashboard(
            InventoryDashboardRequest request) {
        return inventoryDashboardService.getInventoryDashboard(request);
    }
}
