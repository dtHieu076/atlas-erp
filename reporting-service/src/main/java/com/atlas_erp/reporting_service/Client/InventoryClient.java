package com.atlas_erp.reporting_service.Client;

import com.atlas_erp.reporting_service.Dto.External.InventoryStatisticsResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "inventory-service")
public interface InventoryClient {

    // Gọi API thống kê tổng hợp từ Inventory Service
    @GetMapping("/inventory/statistics/summary")
    InventoryStatisticsResponse getInventorySummary(
            @RequestParam("warehouseId") String warehouseId,
            @RequestParam("period") String period);
}