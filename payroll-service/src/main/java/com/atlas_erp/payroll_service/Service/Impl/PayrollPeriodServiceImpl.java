package com.atlas_erp.payroll_service.Service.Impl;

// import com.atlas_erp.payroll_service.Repository.PayrollPeriodRepository;
import com.atlas_erp.payroll_service.Service.PayrollPeriodService;
import com.atlas_erp.payroll_service.Dto.Response.PayrollPeriod.*;
import com.atlas_erp.payroll_service.Dto.Request.PayrollPeriod.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PayrollPeriodServiceImpl implements PayrollPeriodService {

    // private final PayrollPeriodRepository repository;

    @Override
    public PayrollPeriodListResponse list(PayrollPeriodListRequest request) {

        PayrollPeriodListResponse response = new PayrollPeriodListResponse();
        response.setList(List.of()); // list rỗng
        response.setTotal(0L); // tổng = 0

        return response;
    }

    @Override
    public CreatePayrollPeriodResponse create(CreatePayrollPeriodRequest request) {

        CreatePayrollPeriodResponse response = new CreatePayrollPeriodResponse();

        // mock id – tượng trưng
        response.setId(java.util.UUID.randomUUID().toString());

        return response;
    }

    @Override
    public LockPayrollPeriodResponse lock(UUID periodId) {
        return LockPayrollPeriodResponse.success();
    }
}
