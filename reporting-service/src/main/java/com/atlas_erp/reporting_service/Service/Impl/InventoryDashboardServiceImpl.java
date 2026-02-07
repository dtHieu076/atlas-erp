package com.atlas_erp.reporting_service.Service.Impl;

import com.atlas_erp.reporting_service.Cache.serviceCache.ReportCacheService;
import com.atlas_erp.reporting_service.Client.InventoryClient;
import com.atlas_erp.reporting_service.Dto.Request.InventoryDashboardRequest;
import com.atlas_erp.reporting_service.Dto.Response.InventoryDashboardResponse;
import com.atlas_erp.reporting_service.Service.InventoryDashboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class InventoryDashboardServiceImpl implements InventoryDashboardService {

    private final ReportCacheService cacheService;
    private final InventoryClient inventoryClient;

    @Override
    public InventoryDashboardResponse getInventoryDashboard(InventoryDashboardRequest request) {

        // 1. Kiểm tra Cache (Sử dụng warehouseId và period làm key)
        InventoryDashboardResponse cached = cacheService.getInventoryDashboard(
                request.getWarehouseId(), request.getPeriod());
        if (cached != null)
            return cached;

        // 2. Gọi thẳng sang Inventory Service lấy số liệu
        // Không cần validate, nếu warehouseId sai, service kia sẽ trả về data trống (0)
        var stats = inventoryClient.getInventorySummary(request.getWarehouseId(), request.getPeriod());

        // 3. Map dữ liệu vào Response của Reporting Service
        InventoryDashboardResponse response = new InventoryDashboardResponse();
        response.setWarehouseId(request.getWarehouseId());

        if (stats != null) {
            response.setTotalItems(stats.getTotalItems());
            // Ép kiểu BigDecimal sang Number (Double) để hiển thị
            response.setTotalStockQuantity(
                    stats.getTotalStockQuantity() != null ? stats.getTotalStockQuantity().doubleValue() : 0.0);
            response.setLowStockCount(stats.getLowStockCount());
            response.setOverStockCount(stats.getOverStockCount());
            response.setAgingStockCount(stats.getAgingStockCount());
        }

        // 4. Lưu vào Redis và trả về
        cacheService.putInventoryDashboard(response);

        return response;
    }
}