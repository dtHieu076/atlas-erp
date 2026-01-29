package com.atlas_erp.accounting_service.DTO.Request;

import lombok.Data;

@Data
public class FinancialReportRequest {

    private String periodId;
    private String reportType; // BALANCE_SHEET | PNL | CASH_FLOW
}
