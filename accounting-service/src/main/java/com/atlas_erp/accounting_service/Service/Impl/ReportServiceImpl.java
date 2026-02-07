package com.atlas_erp.accounting_service.Service.Impl;

import com.atlas_erp.accounting_service.Cache.serviceCache.AccountingCacheService;
import com.atlas_erp.accounting_service.Dto.Request.Report.*;
import com.atlas_erp.accounting_service.Dto.Response.Report.*;
import com.atlas_erp.accounting_service.Dto.Response.Report.LedgerResponse.LedgerEntry;
import com.atlas_erp.accounting_service.Dto.Response.Report.TrialBalanceResponse.TrialBalanceAccount;
import com.atlas_erp.accounting_service.Entity.AccountEntity;
import com.atlas_erp.accounting_service.Repository.AccountRepository;
import com.atlas_erp.accounting_service.Repository.JournalEntryLineRepository;
import com.atlas_erp.accounting_service.Service.ReportService;
import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportServiceImpl implements ReportService {

    private final JournalEntryLineRepository lineRepository;
    private final AccountRepository accountRepository;
    private final AccountingCacheService cacheService;

    @Override
    public FinancialReportResponse generateFinancialReport(FinancialReportRequest request) {
        // 1. Kiểm tra Cache
        FinancialReportResponse cached = cacheService.getFinancialReport(request.getPeriodId(),
                request.getReportType());
        if (cached != null)
            return cached;

        FinancialReportResponse response = new FinancialReportResponse();
        response.setReportType(request.getReportType());
        response.setPeriodId(request.getPeriodId());
        response.setGeneratedAt(new Date());

        Map<String, BigDecimal> data = new HashMap<>();
        UUID periodUuid = UUID.fromString(request.getPeriodId());

        // 2. Logic tính toán THẬT từ DB
        if ("BALANCE_SHEET".equalsIgnoreCase(request.getReportType())) {
            // Assets = Debit - Credit
            BigDecimal assets = lineRepository.sumNetBalanceByType(periodUuid, "ASSET");
            // Liabilities & Equity thường là số dư bên Có (Credit - Debit) -> Đổi dấu nếu
            // query trả về âm
            BigDecimal liabilities = lineRepository.sumNetBalanceByType(periodUuid, "LIABILITY");
            BigDecimal equity = lineRepository.sumNetBalanceByType(periodUuid, "EQUITY");

            data.put("TOTAL_ASSET", assets != null ? assets : BigDecimal.ZERO);
            data.put("TOTAL_LIABILITY", liabilities != null ? liabilities.negate() : BigDecimal.ZERO);
            data.put("TOTAL_EQUITY", equity != null ? equity.negate() : BigDecimal.ZERO);

        } else if ("PNL".equalsIgnoreCase(request.getReportType())) {
            BigDecimal revenue = lineRepository.sumNetBalanceByType(periodUuid, "REVENUE");
            BigDecimal expense = lineRepository.sumNetBalanceByType(periodUuid, "EXPENSE");

            BigDecimal revValue = revenue != null ? revenue.negate() : BigDecimal.ZERO;
            BigDecimal expValue = expense != null ? expense : BigDecimal.ZERO;

            data.put("TOTAL_REVENUE", revValue);
            data.put("TOTAL_EXPENSE", expValue);
            data.put("NET_INCOME", revValue.subtract(expValue));
        }

        response.setData(data);
        cacheService.putFinancialReport(response);
        return response;
    }

    @Override
    public TrialBalanceResponse trialBalance(TrialBalanceRequest request) {
        TrialBalanceResponse cached = cacheService.getTrialBalance(request.getPeriod());
        if (cached != null)
            return cached;

        UUID periodUuid = UUID.fromString(request.getPeriod());

        // 1. Lấy tất cả phát sinh thực tế từ DB (Chỉ 1 câu Query duy nhất)
        List<Object[]> activities = lineRepository.sumAllAccountActivities(periodUuid);

        // 2. Chuyển đổi sang Map để tra cứu nhanh: Map<AccountId, [Debit, Credit]>
        Map<String, BigDecimal[]> activityMap = new HashMap<>();
        for (Object[] row : activities) {
            activityMap.put((String) row[0], new BigDecimal[] {
                    new BigDecimal(row[1].toString()),
                    new BigDecimal(row[2].toString())
            });
        }

        // 3. Lấy danh sách tài khoản để build báo cáo đầy đủ
        List<AccountEntity> allAccounts = accountRepository.findAll();
        List<TrialBalanceAccount> tbAccounts = allAccounts.stream().map(acc -> {
            TrialBalanceAccount tb = new TrialBalanceAccount();
            String accId = acc.getId().toString();
            tb.setAccountId(accId);

            // Lấy phát sinh từ Map (mặc định 0 nếu không có phát sinh)
            BigDecimal[] values = activityMap.getOrDefault(accId,
                    new BigDecimal[] { BigDecimal.ZERO, BigDecimal.ZERO });

            // Giả định Opening = 0 (Trong thực tế cần query số dư cuối kỳ trước)
            tb.setOpeningBalance(BigDecimal.ZERO);
            tb.setTotalDebit(values[0]);
            tb.setTotalCredit(values[1]);

            // Closing = Opening + Debit - Credit
            tb.setClosingBalance(tb.getOpeningBalance().add(values[0]).subtract(values[1]));

            return tb;
        }).collect(Collectors.toList());

        TrialBalanceResponse response = new TrialBalanceResponse();
        response.setPeriod(request.getPeriod());
        response.setAccounts(tbAccounts);

        cacheService.putTrialBalance(response);
        return response;
    }

    @Override
    public LedgerResponse ledger(LedgerRequest request) {
        // 1. Kiểm tra Cache
        LedgerResponse cached = cacheService.getLedger(request.getAccountId(), request.getPeriod());
        if (cached != null) {
            log.info("🚀 Ledger Cache Hit cho tài khoản: {}", request.getAccountId());
            return cached;
        }

        log.info("🐢 Ledger Cache Miss, đang truy vấn dữ liệu chi tiết...");

        UUID accountUuid = UUID.fromString(request.getAccountId());
        UUID periodUuid = UUID.fromString(request.getPeriod());

        // 2. Lấy dữ liệu thật từ DB (Sắp xếp theo ngày tăng dần)
        List<Object[]> rows = lineRepository.findLedgerData(accountUuid, periodUuid);

        List<LedgerEntry> ledgerEntries = new ArrayList<>();

        // Giả sử số dư đầu kỳ (Opening Balance) lấy từ hệ thống là 0 hoặc query từ kỳ
        // trước
        // Trong ERP gỗ thực tế, giá trị này rất quan trọng
        BigDecimal currentBalance = BigDecimal.ZERO;

        // 3. Tính toán Running Balance
        for (Object[] row : rows) {
            LedgerEntry entry = new LedgerEntry();
            entry.setJournalEntryId(row[0].toString());

            // Convert LocalDate to Date
            LocalDate localDate = (LocalDate) row[1];
            entry.setEntryDate(Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant()));

            entry.setDescription((String) row[2]);

            BigDecimal debit = (BigDecimal) row[3];
            BigDecimal credit = (BigDecimal) row[4];

            entry.setDebit(debit != null ? debit : BigDecimal.ZERO);
            entry.setCredit(credit != null ? credit : BigDecimal.ZERO);

            // Công thức: Số dư mới = Số dư cũ + Nợ - Có
            currentBalance = currentBalance.add(entry.getDebit()).subtract(entry.getCredit());
            entry.setRunningBalance(currentBalance);

            ledgerEntries.add(entry);
        }

        // 4. Build Response
        LedgerResponse response = new LedgerResponse();
        response.setAccountId(request.getAccountId());
        response.setPeriod(request.getPeriod());
        response.setLedgerEntries(ledgerEntries);

        // 5. Lưu vào Cache
        cacheService.putLedger(response);

        return response;
    }
}
