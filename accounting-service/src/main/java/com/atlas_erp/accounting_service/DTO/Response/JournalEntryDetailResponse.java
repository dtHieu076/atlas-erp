package com.atlas_erp.accounting_service.DTO.Response;

import lombok.Data;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

@Data
public class JournalEntryDetailResponse {

    private String journalEntryId;
    private Date entryDate;
    private String description;
    private Date createdAt;
    private String createdBy;
    private List<JournalEntryLine> lines;

    @Data
    public static class JournalEntryLine {
        private String accountId;
        private BigDecimal debit;
        private BigDecimal credit;
    }
}
