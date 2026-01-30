package com.atlas_erp.hr_service.Service;


import com.atlas_erp.hr_service.Dto.Request.Employee.*;
import com.atlas_erp.hr_service.Dto.Response.Employee.*;
import java.util.UUID;

public interface EmployeeService {

    EmployeeListResponse getEmployees(EmployeeListRequest request);

    CreateEmployeeResponse createEmployee(CreateEmployeeRequest request);

    UpdateEmployeeResponse updateEmployee(UUID id, UpdateEmployeeRequest request);

    EmployeeDetailResponse getEmployeeDetail(UUID employeeId);

    EmployeeAssignmentHistoryResponse getAssignmentHistory(UUID employeeId);

    EmployeeTransferResponse transferEmployee(UUID employeeId, EmployeeTransferRequest request);
}

