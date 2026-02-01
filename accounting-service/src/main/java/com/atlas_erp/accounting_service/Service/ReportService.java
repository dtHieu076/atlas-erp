package com.atlas_erp.accounting_service.Service;

import com.atlas_erp.accounting_service.Dto.Request.Report.*;
import com.atlas_erp.accounting_service.Dto.Response.Report.*;

public interface ReportService {

    FinancialReportResponse generateFinancialReport(FinancialReportRequest request);

    TrialBalanceResponse trialBalance(TrialBalanceRequest request);

    LedgerResponse ledger(LedgerRequest request);
}
