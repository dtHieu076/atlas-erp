package com.atlas_erp.payroll_service.Client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.atlas_erp.payroll_service.Dto.Response.getData.*;
import java.util.List;

@FeignClient(name = "hr-service") // Tên service đăng ký trên Eureka
public interface HRServiceClient {

    @GetMapping("/api/v1/internal/payroll-data")
    List<EmployeePayrollDataResponse> getPayrollData(
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate);

    @GetMapping("/api/v1/employees")
    EmployeeListResponse getEmployees(
            @RequestParam(value = "departmentId", required = false) String departmentId,
            @RequestParam(value = "status", required = false) String status,
            @RequestParam(value = "page") Integer page,
            @RequestParam(value = "pageSize") Integer pageSize);
}