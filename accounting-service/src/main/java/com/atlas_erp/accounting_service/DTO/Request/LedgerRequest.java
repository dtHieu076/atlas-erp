package com.atlas_erp.accounting_service.DTO.Request;

import lombok.Data;

@Data
public class LedgerRequest {

    private String accountId;
    private String period;
}
