package com.atlas_erp.production_service.Controller;

import com.atlas_erp.production_service.Dto.Request.ProductionStatistics.ProductionStatisticsSummaryRequest;
import com.atlas_erp.production_service.Dto.Response.ProductionStatistics.ProductionStatisticsSummaryResponse;
import com.atlas_erp.production_service.Service.ProductionStatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/production/statistics")
@RequiredArgsConstructor
public class ProductionStatisticsController {

    private final ProductionStatisticsService productionStatisticsService;

    /**
     * GET /production/statistics/summary
     */
    @GetMapping("/summary")
    public ProductionStatisticsSummaryResponse getStatisticsSummary(
            ProductionStatisticsSummaryRequest request) {
        return productionStatisticsService.getProductionStatisticsSummary(request);
    }
}
