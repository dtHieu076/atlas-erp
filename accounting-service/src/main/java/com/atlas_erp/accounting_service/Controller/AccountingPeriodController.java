package com.atlas_erp.accounting_service.Controller;

import com.atlas_erp.accounting_service.Dto.Request.LockAccountingPeriodRequest;
import com.atlas_erp.accounting_service.Dto.Response.LockAccountingPeriodResponse;
import com.atlas_erp.accounting_service.Service.AccountingPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/periods")
@RequiredArgsConstructor
public class AccountingPeriodController {

    private final AccountingPeriodService accountingPeriodService;

    @PostMapping("/{id}/lock")
    public LockAccountingPeriodResponse lockPeriod(
            @PathVariable String id,
            @RequestBody LockAccountingPeriodRequest request) {
        return accountingPeriodService.lockPeriod(id, request);
    }
}
