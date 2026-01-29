package com.atlas_erp.accounting_service.DTO.Response;

import lombok.Data;
import java.util.List;
import java.util.Date;
import java.math.BigDecimal;

@Data
public class JournalEntryListResponse {

    private List<JournalEntrySummary> journalEntries;

    @Data
    public static class JournalEntrySummary {
        private String journalEntryId;
        private Date entryDate;
        private String description;
        private BigDecimal totalDebit;
        private BigDecimal totalCredit;
        private String status;
    }
}
