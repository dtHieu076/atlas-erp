package com.atlas_erp.hr_service.Controller;

import com.atlas_erp.hr_service.Dto.Request.Employee.*;
import com.atlas_erp.hr_service.Dto.Response.ApiResponse;
import com.atlas_erp.hr_service.Dto.Response.Employee.EmployeeProfileResponse;
import com.atlas_erp.hr_service.Dto.Response.Employee.EmployeeResponse;
import com.atlas_erp.hr_service.Dto.Response.Employee_v0.*;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.atlas_erp.hr_service.Service.EmployeeService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // 1️⃣ GET /employees
    @GetMapping
    public ApiResponse<List<EmployeeResponse>> getAllEmployees() {
        return ApiResponse.success(employeeService.getAllEmployees());
    }

    // 3️⃣ PUT /employees/{id}
    @GetMapping("/{id}")
    public ApiResponse<EmployeeProfileResponse> getEmployeeProfile(@PathVariable UUID id) {
        return ApiResponse.success(employeeService.getEmployeeProfile(id));
    }

    // -------------------------------------------------------------
    // 2️⃣ POST /employees
    @PostMapping
    public CreateEmployeeResponse createEmployee(
            @RequestBody CreateEmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    // 12️⃣ GET /employees/{id}/assignments
    @GetMapping("/{id}/assignments")
    public EmployeeAssignmentHistoryResponse getAssignmentHistory(
            @PathVariable UUID id) {
        return employeeService.getAssignmentHistory(id);
    }

    // 13️⃣ POST /employees/{id}/transfer
    @PostMapping("/{id}/transfer")
    public EmployeeTransferResponse transferEmployee(
            @PathVariable UUID id,
            @RequestBody EmployeeTransferRequest request) {
        return employeeService.transferEmployee(id, request);
    }

    @GetMapping("/statistics/summary")
    public EmployeeStatisticsResponse getEmployeeStatistics(EmployeeStatisticsRequest request) {
        return employeeService.getEmployeeStatistics(request);
    }

    @PostMapping("/summary/batch")
    public ApiResponse<List<EmployeeSummaryItemDTO>> getEmployeeSummaryBatch(
            @RequestBody EmployeeBatchRequest request) {

        return employeeService.getEmployeeSummaryBatch(request);
    }

}
