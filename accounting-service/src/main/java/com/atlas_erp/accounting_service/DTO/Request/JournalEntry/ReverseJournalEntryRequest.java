package com.atlas_erp.accounting_service.Dto.Request.JournalEntry;

import lombok.Data;

@Data
public class ReverseJournalEntryRequest {

    private String reason;
    private String reversedBy;
}
