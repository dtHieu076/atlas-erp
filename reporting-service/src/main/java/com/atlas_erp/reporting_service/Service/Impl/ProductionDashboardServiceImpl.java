package com.atlas_erp.reporting_service.Service.Impl;

import com.atlas_erp.reporting_service.Dto.Request.ProductionDashboardRequest;
import com.atlas_erp.reporting_service.Dto.Response.ProductionDashboardResponse;
import com.atlas_erp.reporting_service.Service.ProductionDashboardService;
import org.springframework.stereotype.Service;
import java.util.Date;
import java.math.*;

@Service
public class ProductionDashboardServiceImpl
        implements ProductionDashboardService {

    @Override
    public ProductionDashboardResponse getProductionDashboard(
            ProductionDashboardRequest request) {

        ProductionDashboardResponse response = new ProductionDashboardResponse();

        response.setTotalPlanQty(0);
        response.setTotalActualQty(0);
        response.setDefectRate(BigDecimal.ZERO);
        response.setOnTimeRate(BigDecimal.ZERO);
        response.setUtilizationRate(BigDecimal.ZERO);
        response.setSnapshotDate(new Date());

        return response;
    }
}
