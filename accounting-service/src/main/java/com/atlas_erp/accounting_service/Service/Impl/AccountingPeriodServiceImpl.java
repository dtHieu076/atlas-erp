package com.atlas_erp.accounting_service.Service.Impl;

import com.atlas_erp.accounting_service.Dto.Request.LockAccountingPeriodRequest;
import com.atlas_erp.accounting_service.Dto.Response.LockAccountingPeriodResponse;
import com.atlas_erp.accounting_service.Service.AccountingPeriodService;
import org.springframework.stereotype.Service;

@Service
public class AccountingPeriodServiceImpl implements AccountingPeriodService {

    @Override
    public LockAccountingPeriodResponse lockPeriod(String id, LockAccountingPeriodRequest request) {
        LockAccountingPeriodResponse response = new LockAccountingPeriodResponse();
        response.setSuccess(true);
        return response;
    }
}
