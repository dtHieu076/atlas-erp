package com.atlas_erp.payroll_service.Service;

import com.atlas_erp.payroll_service.Dto.Request.PayrollPeriod.CreatePayrollPeriodRequest;
import com.atlas_erp.payroll_service.Dto.Request.PayrollPeriod.PayrollPeriodListRequest;
import com.atlas_erp.payroll_service.Dto.Response.PayrollPeriod.CreatePayrollPeriodResponse;
import com.atlas_erp.payroll_service.Dto.Response.PayrollPeriod.LockPayrollPeriodResponse;
import com.atlas_erp.payroll_service.Dto.Response.PayrollPeriod.PayrollPeriodListResponse;
import java.util.UUID;

public interface PayrollPeriodService {

    PayrollPeriodListResponse list(PayrollPeriodListRequest request);

    CreatePayrollPeriodResponse create(CreatePayrollPeriodRequest request);

    LockPayrollPeriodResponse lock(UUID periodId);
}
