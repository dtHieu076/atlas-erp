package com.atlas_erp.accounting_service.Dto.Request.Report;

import lombok.Data;

@Data
public class FinancialReportRequest {

    private String periodId;
    private String reportType; // BALANCE_SHEET | PNL | CASH_FLOW
}
