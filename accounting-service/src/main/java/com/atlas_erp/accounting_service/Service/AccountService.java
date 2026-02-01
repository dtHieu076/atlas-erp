package com.atlas_erp.accounting_service.Service;

import com.atlas_erp.accounting_service.Dto.Request.Account.AccountListRequest;
import com.atlas_erp.accounting_service.Dto.Response.Account.AccountListResponse;

public interface AccountService {
    AccountListResponse getAccounts(AccountListRequest request);
}
