package com.atlas_erp.production_service.Service;

import com.atlas_erp.production_service.Dto.Request.ProductionStatistics.ProductionStatisticsSummaryRequest;
import com.atlas_erp.production_service.Dto.Response.ProductionStatistics.ProductionStatisticsSummaryResponse;

public interface ProductionStatisticsService {

    /**
     * Thống kê KPI sản xuất theo period
     */
    ProductionStatisticsSummaryResponse getProductionStatisticsSummary(
            ProductionStatisticsSummaryRequest request);
}
