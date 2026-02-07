package com.atlas_erp.reporting_service.Dto.External;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;

@Data
public class FinancialReportResponse {
    private String reportType;
    private String periodId;
    private Map<String, BigDecimal> data; // Chứa TOTAL_REVENUE, TOTAL_EXPENSE
}