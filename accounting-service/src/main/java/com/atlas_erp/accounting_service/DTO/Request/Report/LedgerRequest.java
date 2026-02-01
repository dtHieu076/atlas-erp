package com.atlas_erp.accounting_service.Dto.Request.Report;

import lombok.Data;

@Data
public class LedgerRequest {

    private String accountId;
    private String period;
}
