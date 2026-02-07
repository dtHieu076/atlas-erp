package com.atlas_erp.hr_service.Controller;

import com.atlas_erp.hr_service.Dto.Request.Employee.*;
import com.atlas_erp.hr_service.Dto.Response.Employee.*;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import com.atlas_erp.hr_service.Service.EmployeeService;
import java.util.UUID;

@RestController
@RequestMapping("/employees")
@RequiredArgsConstructor
public class EmployeeController {

    private final EmployeeService employeeService;

    // 1️⃣ GET /employees
    @GetMapping
    public EmployeeListResponse getEmployees(EmployeeListRequest request) {
        return employeeService.getEmployees(request);
    }

    // 2️⃣ POST /employees
    @PostMapping
    public CreateEmployeeResponse createEmployee(
            @RequestBody CreateEmployeeRequest request) {
        return employeeService.createEmployee(request);
    }

    // 3️⃣ PUT /employees/{id}
    @PutMapping("/{id}")
    public UpdateEmployeeResponse updateEmployee(
            @PathVariable UUID id,
            @RequestBody UpdateEmployeeRequest request) {
        return employeeService.updateEmployee(id, request);
    }

    // 11️⃣ GET /employees/{id}
    @GetMapping("/{id}")
    public EmployeeDetailResponse getEmployeeDetail(
            @PathVariable UUID id) {
        return employeeService.getEmployeeDetail(id);
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
}
