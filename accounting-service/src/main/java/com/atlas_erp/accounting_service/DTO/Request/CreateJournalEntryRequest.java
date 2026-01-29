package com.atlas_erp.accounting_service.DTO.Request;

import lombok.Data;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

@Data
public class CreateJournalEntryRequest {

    private Date entryDate;
    private String description;
    private String periodId;
    private String createdBy;
    private List<JournalLine> lines;

    @Data
    public static class JournalLine {
        private String accountId;
        private BigDecimal debit;
        private BigDecimal credit;
        private String note;
    }
}
