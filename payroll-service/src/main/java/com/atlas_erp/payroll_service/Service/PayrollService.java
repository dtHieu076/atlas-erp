package com.atlas_erp.payroll_service.Service;

import com.atlas_erp.payroll_service.Dto.Request.Payroll.*;
import com.atlas_erp.payroll_service.Dto.Response.Payroll.*;
import java.util.UUID;

public interface PayrollService {

    PayrollCalculateResponse calculate(PayrollCalculateRequest request);

    PayrollListResponse list(PayrollListRequest request);

    PayrollRecalculateResponse recalculate(PayrollRecalculateRequest request);

    ApprovePayrollResponse approve(UUID payrollId, ApprovePayrollRequest request);

    RejectPayrollResponse reject(UUID payrollId, RejectPayrollRequest request);

    PayrollDetailResponse detail(UUID payrollId);
}
