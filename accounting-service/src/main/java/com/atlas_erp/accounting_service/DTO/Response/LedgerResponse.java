package com.atlas_erp.accounting_service.DTO.Response;

import lombok.Data;
import java.util.List;
import java.util.Date;
import java.math.BigDecimal;

@Data
public class LedgerResponse {

    private String accountId;
    private String period;
    private List<LedgerEntry> ledgerEntries;

    @Data
    public static class LedgerEntry {
        private String journalEntryId;
        private Date entryDate;
        private String description;
        private BigDecimal debit;
        private BigDecimal credit;
        private BigDecimal runningBalance;
    }
}
