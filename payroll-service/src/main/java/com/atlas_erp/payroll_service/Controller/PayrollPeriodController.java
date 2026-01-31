package com.atlas_erp.payroll_service.Controller;

import com.atlas_erp.payroll_service.Dto.Request.PayrollPeriod.*;
import com.atlas_erp.payroll_service.Dto.Response.PayrollPeriod.*;
import com.atlas_erp.payroll_service.Service.PayrollPeriodService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/periods")
@RequiredArgsConstructor
public class PayrollPeriodController {

    private final PayrollPeriodService service;

    @GetMapping
    public PayrollPeriodListResponse list(PayrollPeriodListRequest request) {
        return service.list(request);
    }

    @PostMapping
    public CreatePayrollPeriodResponse create(
            @RequestBody CreatePayrollPeriodRequest request) {
        return service.create(request);
    }

    @PostMapping("/{id}/lock")
    public LockPayrollPeriodResponse lock(@PathVariable UUID id) {
        return service.lock(id);
    }
}
