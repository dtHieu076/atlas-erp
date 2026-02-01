package com.atlas_erp.accounting_service.Dto.Request.JournalEntry;

import lombok.Data;
import java.util.Date;

@Data
public class JournalEntryListRequest {

    private Date fromDate;
    private Date toDate;
    private String accountId;
    private Integer page;
}
