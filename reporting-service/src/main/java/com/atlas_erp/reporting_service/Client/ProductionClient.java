package com.atlas_erp.reporting_service.Client;

import com.atlas_erp.reporting_service.Dto.External.ProductionStatisticsResponse;
import com.atlas_erp.reporting_service.Dto.External.ProductionSummaryResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "production-service", url = "${app.services.production-url}")
public interface ProductionClient {
    @GetMapping("/production/summary")
    ProductionSummaryResponse getProductionSummary(
            @RequestParam("period") String period);

    @GetMapping("/production/statistics/summary")
    ProductionStatisticsResponse getProductionSummary(
            @RequestParam("periodType") String periodType,
            @RequestParam("periodValue") String periodValue,
            @RequestParam(value = "plantId", required = false) String plantId);
}