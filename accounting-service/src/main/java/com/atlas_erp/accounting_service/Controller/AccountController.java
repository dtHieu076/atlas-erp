package com.atlas_erp.accounting_service.Controller;

import com.atlas_erp.accounting_service.Dto.Request.Account.AccountListRequest;
import com.atlas_erp.accounting_service.Dto.Response.Account.AccountListResponse;
import com.atlas_erp.accounting_service.Service.AccountService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/accounts")
@RequiredArgsConstructor
public class AccountController {

    private final AccountService accountService;

    @GetMapping
    public AccountListResponse getAccounts(AccountListRequest request) {
        return accountService.getAccounts(request);
    }
}
