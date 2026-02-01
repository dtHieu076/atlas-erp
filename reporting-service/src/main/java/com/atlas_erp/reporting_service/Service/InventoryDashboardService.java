package com.atlas_erp.reporting_service.Service;

import com.atlas_erp.reporting_service.Dto.Request.InventoryDashboardRequest;
import com.atlas_erp.reporting_service.Dto.Response.InventoryDashboardResponse;

public interface InventoryDashboardService {

    InventoryDashboardResponse getInventoryDashboard(
            InventoryDashboardRequest request);
}
