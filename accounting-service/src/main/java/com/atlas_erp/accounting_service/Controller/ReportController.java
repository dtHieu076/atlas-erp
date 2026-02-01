package com.atlas_erp.accounting_service.Controller;

import com.atlas_erp.accounting_service.Dto.Request.Report.*;
import com.atlas_erp.accounting_service.Dto.Response.Report.*;
import com.atlas_erp.accounting_service.Service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;

    @GetMapping("/financial")
    public FinancialReportResponse financialReport(FinancialReportRequest request) {
        return reportService.generateFinancialReport(request);
    }

    @GetMapping("/trial-balance")
    public TrialBalanceResponse trialBalance(TrialBalanceRequest request) {
        return reportService.trialBalance(request);
    }

    @GetMapping("/ledger")
    public LedgerResponse ledger(LedgerRequest request) {
        return reportService.ledger(request);
    }
}
