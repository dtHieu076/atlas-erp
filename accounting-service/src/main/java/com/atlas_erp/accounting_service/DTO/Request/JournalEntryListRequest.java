package com.atlas_erp.accounting_service.DTO.Request;

import lombok.Data;
import java.util.Date;

@Data
public class JournalEntryListRequest {

    private Date fromDate;
    private Date toDate;
    private String accountId;
    private Integer page;
}
