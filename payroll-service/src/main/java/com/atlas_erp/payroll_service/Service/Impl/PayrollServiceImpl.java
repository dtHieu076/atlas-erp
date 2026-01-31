package com.atlas_erp.payroll_service.Service.Impl;

import com.atlas_erp.payroll_service.Dto.PayrollDetailItemDTO;
import com.atlas_erp.payroll_service.Dto.Request.Payroll.*;
import com.atlas_erp.payroll_service.Dto.Response.Payroll.*;
import com.atlas_erp.payroll_service.Service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PayrollServiceImpl implements PayrollService {

        // private final PayrollRepository payrollRepository;
        // private final PayrollComponentRepository componentRepository;

        @Override
        public PayrollCalculateResponse calculate(PayrollCalculateRequest request) {

                PayrollCalculateResponse response = new PayrollCalculateResponse();

                response.setPeriodId(request.getPeriodId());
                response.setTotalEmployee(0); // mock
                response.setTotalAmount(BigDecimal.ZERO); // mock
                response.setStatus("CALCULATED"); // mock status
                response.setCalculatedAt(LocalDateTime.now()); // đúng kiểu LocalDateTime

                return response;
        }

        @Override
        public PayrollListResponse list(PayrollListRequest request) {

                PayrollDetailItemDTO item = new PayrollDetailItemDTO();
                item.setEmployeeId("EMP-001");
                item.setEmployeeCode("NV001");
                item.setFullName("Nguyen Van A");
                item.setBaseSalary(BigDecimal.valueOf(10_000_000));
                item.setOvertimePay(BigDecimal.valueOf(1_000_000));
                item.setAllowance(BigDecimal.valueOf(500_000));
                item.setDeduction(BigDecimal.ZERO);
                item.setNetSalary(BigDecimal.valueOf(11_500_000));
                item.setStatus("CALCULATED");

                PayrollListResponse response = new PayrollListResponse();
                response.setList(List.of(item));
                response.setTotal(1L);

                return response;
        }

        @Override
        public PayrollRecalculateResponse recalculate(PayrollRecalculateRequest request) {

                PayrollRecalculateResponse response = new PayrollRecalculateResponse();

                response.setResult("OK"); // mock result
                response.setRecalculatedCount(
                                request.getPayrollId() != null ? 1 : 0 // mock logic nhẹ
                );
                response.setPeriodId(request.getPeriodId());
                response.setMessage("Recalculated");

                return response;
        }

        @Override
        public ApprovePayrollResponse approve(UUID payrollId, ApprovePayrollRequest request) {

                ApprovePayrollResponse response = new ApprovePayrollResponse();

                response.setSuccess(true); // mock approve OK
                response.setPayrollId(payrollId.toString());
                response.setStatus("APPROVED");
                response.setApprovedAt(LocalDateTime.now());
                response.setApprovedBy("SYSTEM"); // mock user

                return response;
        }

        @Override
        public RejectPayrollResponse reject(UUID payrollId, RejectPayrollRequest request) {

                RejectPayrollResponse response = new RejectPayrollResponse();

                response.setSuccess(true); // mock reject OK
                response.setPayrollId(payrollId.toString());
                response.setStatus("REJECTED");
                response.setRejectedAt(LocalDateTime.now());
                response.setRejectedBy("SYSTEM"); // mock user

                return response;
        }

        @Override
        public PayrollDetailResponse detail(UUID payrollId) {

                PayrollDetailResponse response = new PayrollDetailResponse();

                response.setPayrollId(payrollId.toString());
                response.setEmployeeId("EMP-001"); // mock
                response.setPeriodId("2025-01"); // mock

                response.setBaseSalary(BigDecimal.valueOf(10_000_000));
                response.setOvertimePay(BigDecimal.valueOf(1_000_000));
                response.setBonus(BigDecimal.valueOf(500_000));
                response.setDeductions(BigDecimal.valueOf(300_000));
                response.setNetSalary(BigDecimal.valueOf(11_200_000));

                response.setCurrency("VND");
                response.setStatus("CALCULATED");

                response.setGeneratedAt(LocalDateTime.now().minusDays(1));
                response.setLastUpdatedAt(LocalDateTime.now());

                return response;
        }

}
