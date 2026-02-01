package com.atlas_erp.accounting_service.Service.Impl;

import com.atlas_erp.accounting_service.Dto.Request.Account.AccountListRequest;
import com.atlas_erp.accounting_service.Dto.Response.Account.AccountListResponse;
import com.atlas_erp.accounting_service.Service.AccountService;
import org.springframework.stereotype.Service;
import java.util.Collections;

@Service
public class AccountServiceImpl implements AccountService {

    @Override
    public AccountListResponse getAccounts(AccountListRequest request) {
        AccountListResponse response = new AccountListResponse();
        response.setList(Collections.emptyList());
        return response;
    }
}
