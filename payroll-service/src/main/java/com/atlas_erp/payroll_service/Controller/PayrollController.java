package com.atlas_erp.payroll_service.Controller;

import com.atlas_erp.payroll_service.Dto.Request.Payroll.*;
import com.atlas_erp.payroll_service.Dto.Response.Payroll.*;
import com.atlas_erp.payroll_service.Service.PayrollService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/payroll")
@RequiredArgsConstructor
public class PayrollController {

    private final PayrollService service;

    @PostMapping("/calculate")
    public PayrollCalculateResponse calculate(
            @RequestBody PayrollCalculateRequest request) {
        return service.calculate(request);
    }

    @GetMapping
    public PayrollListResponse list(PayrollListRequest request) {
        return service.list(request);
    }

    @PostMapping("/recalculate")
    public PayrollRecalculateResponse recalculate(
            @RequestBody PayrollRecalculateRequest request) {
        return service.recalculate(request);
    }

    @PostMapping("/{id}/approve")
    public ApprovePayrollResponse approve(
            @PathVariable UUID id,
            @RequestBody ApprovePayrollRequest request) {
        return service.approve(id, request);
    }

    @PostMapping("/{id}/reject")
    public RejectPayrollResponse reject(
            @PathVariable UUID id,
            @RequestBody RejectPayrollRequest request) {
        return service.reject(id, request);
    }

    @GetMapping("/{id}")
    public PayrollDetailResponse detail(@PathVariable UUID id) {
        return service.detail(id);
    }
}
