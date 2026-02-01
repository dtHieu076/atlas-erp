package com.atlas_erp.accounting_service.Dto.Request.Account;

import lombok.Data;

@Data
public class AccountListRequest {

    private String accountType;
    private String parentId;
}
