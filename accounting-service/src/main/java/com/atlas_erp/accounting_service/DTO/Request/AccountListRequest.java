package com.atlas_erp.accounting_service.DTO.Request;

import lombok.Data;

@Data
public class AccountListRequest {

    private String accountType;
    private String parentId;
}
