package com.atlas_erp.reporting_service.Client;

import com.atlas_erp.reporting_service.Dto.External.FinancialReportResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "accounting-service", url = "${app.services.accounting-url}")
public interface AccountingClient {
    @GetMapping("/reports/financial")
    FinancialReportResponse getFinancialData(
            @RequestParam("periodId") String periodId,
            @RequestParam("reportType") String reportType);
}