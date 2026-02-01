package com.atlas_erp.accounting_service.Service.Impl;

import com.atlas_erp.accounting_service.Dto.Request.Report.*;
import com.atlas_erp.accounting_service.Dto.Response.Report.*;
import com.atlas_erp.accounting_service.Service.ReportService;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Date;
import java.util.Map;

@Service
public class ReportServiceImpl implements ReportService {

    @Override
    public FinancialReportResponse generateFinancialReport(FinancialReportRequest request) {

        FinancialReportResponse response = new FinancialReportResponse();

        response.setReportType(request.getReportType());
        response.setPeriodId(request.getPeriodId());
        response.setGeneratedAt(new Date());

        // dữ liệu tượng trưng cho báo cáo
        Map<String, BigDecimal> data = new HashMap<>();
        data.put("TOTAL_ASSET", BigDecimal.ZERO);
        data.put("TOTAL_LIABILITY", BigDecimal.ZERO);
        data.put("TOTAL_EQUITY", BigDecimal.ZERO);

        response.setData(data);

        return response;
    }

    @Override
    public TrialBalanceResponse trialBalance(TrialBalanceRequest request) {
        return new TrialBalanceResponse();
    }

    @Override
    public LedgerResponse ledger(LedgerRequest request) {
        return new LedgerResponse();
    }
}
