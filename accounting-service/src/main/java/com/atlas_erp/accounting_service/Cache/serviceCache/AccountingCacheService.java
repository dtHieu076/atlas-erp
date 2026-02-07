package com.atlas_erp.accounting_service.Cache.serviceCache;

import com.atlas_erp.accounting_service.Dto.Response.Account.AccountListResponse;
import com.atlas_erp.accounting_service.Dto.Response.JournalEntry.JournalEntryDetailResponse;
import com.atlas_erp.accounting_service.Dto.Response.JournalEntry.JournalEntryListResponse;
import com.atlas_erp.accounting_service.Dto.Response.Report.FinancialReportResponse;
import com.atlas_erp.accounting_service.Dto.Response.Report.LedgerResponse;
import com.atlas_erp.accounting_service.Dto.Response.Report.TrialBalanceResponse;
import com.atlas_erp.accounting_service.Dto.Request.JournalEntry.JournalEntryListRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountingCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration TTL_COA = Duration.ofHours(1);
    private static final Duration TTL_JOURNAL = Duration.ofMinutes(10); // Bút toán thay đổi nhiều hơn

    private static final String COA_LIST_KEY = "accounting:coa:list:";
    private static final String JOURNAL_LIST_KEY = "accounting:journal:list:";

    // --- COA Cache Logic ---
    private String generateCoaKey(String accountType, String parentId) {
        return COA_LIST_KEY + accountType + ":" + parentId;
    }

    public AccountListResponse getCoaList(String accountType, String parentId) {
        try {
            return (AccountListResponse) redisTemplate.opsForValue().get(generateCoaKey(accountType, parentId));
        } catch (Exception e) {
            log.error("Redis Get COA Error: {}", e.getMessage());
            return null;
        }
    }

    public void putCoaList(String accountType, String parentId, AccountListResponse response) {
        try {
            redisTemplate.opsForValue().set(generateCoaKey(accountType, parentId), response, TTL_COA);
        } catch (Exception e) {
            log.error("Redis Put COA Error: {}", e.getMessage());
        }
    }

    public void evictCoa() {
        var keys = redisTemplate.keys(COA_LIST_KEY + "*");
        if (keys != null)
            redisTemplate.delete(keys);
    }

    // --- Journal Entry Cache Logic ---
    private String generateJournalKey(JournalEntryListRequest request) {
        return JOURNAL_LIST_KEY + String.format("%s:%s:%s:%d",
                request.getFromDate(),
                request.getToDate(),
                request.getAccountId(),
                request.getPage());
    }

    public JournalEntryListResponse getJournalList(JournalEntryListRequest request) {
        try {
            return (JournalEntryListResponse) redisTemplate.opsForValue().get(generateJournalKey(request));
        } catch (Exception e) {
            log.error("Redis Get Journal Error: {}", e.getMessage());
            return null;
        }
    }

    public void putJournalList(JournalEntryListRequest request, JournalEntryListResponse response) {
        try {
            redisTemplate.opsForValue().set(generateJournalKey(request), response, TTL_JOURNAL);
        } catch (Exception e) {
            log.error("Redis Put Journal Error: {}", e.getMessage());
        }
    }

    public void evictJournals() {
        var keys = redisTemplate.keys(JOURNAL_LIST_KEY + "*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
    }// Bổ sung vào AccountingCacheService.java

    private static final String JOURNAL_DETAIL_PREFIX = "accounting:journal:detail:";
    private static final Duration TTL_DETAIL = Duration.ofMinutes(30);

    private String generateJournalDetailKey(String id) {
        return JOURNAL_DETAIL_PREFIX + id;
    }

    public JournalEntryDetailResponse getJournalDetail(String id) {
        try {
            return (JournalEntryDetailResponse) redisTemplate.opsForValue().get(generateJournalDetailKey(id));
        } catch (Exception e) {
            log.error("Redis Get Journal Detail Error: {}", e.getMessage());
            return null;
        }
    }

    public void putJournalDetail(String id, JournalEntryDetailResponse response) {
        try {
            redisTemplate.opsForValue().set(generateJournalDetailKey(id), response, TTL_DETAIL);
        } catch (Exception e) {
            log.error("Redis Put Journal Detail Error: {}", e.getMessage());
        }
    }

    public void evictJournalDetail(String id) {
        redisTemplate.delete(generateJournalDetailKey(id));
    }// Bổ sung vào AccountingCacheService.java

    private static final String REPORT_PREFIX = "accounting:report:";
    private static final Duration TTL_REPORT = Duration.ofMinutes(15);

    private String generateReportKey(String periodId, String reportType) {
        return REPORT_PREFIX + reportType + ":" + periodId;
    }

    public FinancialReportResponse getFinancialReport(String periodId, String reportType) {
        try {
            return (FinancialReportResponse) redisTemplate.opsForValue().get(generateReportKey(periodId, reportType));
        } catch (Exception e) {
            log.error("Redis Get Report Error: {}", e.getMessage());
            return null;
        }
    }

    public void putFinancialReport(FinancialReportResponse response) {
        try {
            redisTemplate.opsForValue().set(
                    generateReportKey(response.getPeriodId(), response.getReportType()),
                    response,
                    TTL_REPORT);
        } catch (Exception e) {
            log.error("Redis Put Report Error: {}", e.getMessage());
        }
    }

    public void evictReports(String periodId) {
        var keys = redisTemplate.keys(REPORT_PREFIX + "*:" + periodId);
        if (keys != null)
            redisTemplate.delete(keys);
    }// Bổ sung vào AccountingCacheService.java

    private static final String TRIAL_BALANCE_KEY = "accounting:report:trial-balance:";
    private static final Duration TTL_TRIAL_BALANCE = Duration.ofMinutes(20);

    public TrialBalanceResponse getTrialBalance(String periodId) {
        try {
            return (TrialBalanceResponse) redisTemplate.opsForValue().get(TRIAL_BALANCE_KEY + periodId);
        } catch (Exception e) {
            log.error("Redis Get Trial Balance Error: {}", e.getMessage());
            return null;
        }
    }

    public void putTrialBalance(TrialBalanceResponse response) {
        try {
            redisTemplate.opsForValue().set(
                    TRIAL_BALANCE_KEY + response.getPeriod(),
                    response,
                    TTL_TRIAL_BALANCE);
        } catch (Exception e) {
            log.error("Redis Put Trial Balance Error: {}", e.getMessage());
        }
    }

    public void evictTrialBalance(String periodId) {
        redisTemplate.delete(TRIAL_BALANCE_KEY + periodId);
    }// Bổ sung vào AccountingCacheService.java

    private static final String LEDGER_KEY = "accounting:report:ledger:";
    private static final Duration TTL_LEDGER = Duration.ofMinutes(15);

    public LedgerResponse getLedger(String accountId, String periodId) {
        try {
            return (LedgerResponse) redisTemplate.opsForValue().get(LEDGER_KEY + accountId + ":" + periodId);
        } catch (Exception e) {
            log.error("Redis Get Ledger Error: {}", e.getMessage());
            return null;
        }
    }

    public void putLedger(LedgerResponse response) {
        try {
            redisTemplate.opsForValue().set(
                    LEDGER_KEY + response.getAccountId() + ":" + response.getPeriod(),
                    response,
                    TTL_LEDGER);
        } catch (Exception e) {
            log.error("Redis Put Ledger Error: {}", e.getMessage());
        }
    }
}