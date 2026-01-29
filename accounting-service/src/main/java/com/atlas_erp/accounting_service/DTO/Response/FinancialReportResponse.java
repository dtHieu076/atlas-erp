package com.atlas_erp.accounting_service.DTO.Response;

import lombok.Data;
import java.util.Date;
import java.util.Map;
import java.math.BigDecimal;

@Data
public class FinancialReportResponse {

    private String reportType;
    private String periodId;
    private Date generatedAt;
    private Map<String, BigDecimal> data;
}
