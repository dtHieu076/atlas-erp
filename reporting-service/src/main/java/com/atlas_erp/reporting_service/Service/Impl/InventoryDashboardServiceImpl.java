package com.atlas_erp.reporting_service.Service.Impl;

import com.atlas_erp.reporting_service.Dto.Request.InventoryDashboardRequest;
import com.atlas_erp.reporting_service.Dto.Response.InventoryDashboardResponse;
import com.atlas_erp.reporting_service.Service.InventoryDashboardService;
import org.springframework.stereotype.Service;

@Service
public class InventoryDashboardServiceImpl
        implements InventoryDashboardService {

    @Override
    public InventoryDashboardResponse getInventoryDashboard(
            InventoryDashboardRequest request) {

        return new InventoryDashboardResponse();
    }
}
