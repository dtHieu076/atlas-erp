package com.atlas_erp.accounting_service.Dto.Response.Account;

import lombok.Data;
import java.util.List;

@Data
public class AccountListResponse {

    private List<AccountDto> list;

    @Data
    public static class AccountDto {
        private String id;
        private String accountCode;
        private String accountName;
        private String accountType;
        private String parentId;
        private Integer level;
        private String status;
    }
}
