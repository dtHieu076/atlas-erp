package com.atlas_erp.reporting_service.Service;

import com.atlas_erp.reporting_service.Dto.Request.ProductionDashboardRequest;
import com.atlas_erp.reporting_service.Dto.Response.ProductionDashboardResponse;

public interface ProductionDashboardService {

    ProductionDashboardResponse getProductionDashboard(
            ProductionDashboardRequest request);
}
