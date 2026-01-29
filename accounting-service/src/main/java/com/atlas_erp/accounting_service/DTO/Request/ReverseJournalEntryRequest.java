package com.atlas_erp.accounting_service.DTO.Request;

import lombok.Data;

@Data
public class ReverseJournalEntryRequest {

    private String reason;
    private String reversedBy;
}
