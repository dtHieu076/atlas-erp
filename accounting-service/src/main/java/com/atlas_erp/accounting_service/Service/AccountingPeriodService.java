package com.atlas_erp.accounting_service.Service;

import com.atlas_erp.accounting_service.Dto.Request.LockAccountingPeriodRequest;
import com.atlas_erp.accounting_service.Dto.Response.LockAccountingPeriodResponse;

public interface AccountingPeriodService {
    LockAccountingPeriodResponse lockPeriod(String id, LockAccountingPeriodRequest request);
}
