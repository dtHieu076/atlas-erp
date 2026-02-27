package com.atlas_erp.hr_service.Service;

import com.atlas_erp.hr_service.Dto.Request.Employee.*;
import com.atlas_erp.hr_service.Dto.Response.ApiResponse;
import com.atlas_erp.hr_service.Dto.Response.Employee.EmployeeProfileResponse;
import com.atlas_erp.hr_service.Dto.Response.Employee.EmployeeResponse;
import com.atlas_erp.hr_service.Dto.Response.Employee_v0.*;

import java.util.List;
import java.util.UUID;

public interface EmployeeService {

    List<EmployeeResponse> getAllEmployees();

    EmployeeProfileResponse getEmployeeProfile(UUID id);

    CreateEmployeeResponse createEmployee(CreateEmployeeRequest request);

    UpdateEmployeeResponse updateEmployee(UUID id, UpdateEmployeeRequest request);

    EmployeeAssignmentHistoryResponse getAssignmentHistory(UUID employeeId);

    EmployeeTransferResponse transferEmployee(UUID employeeId, EmployeeTransferRequest request);

    // Thêm vào EmployeeService interface
    EmployeeStatisticsResponse getEmployeeStatistics(EmployeeStatisticsRequest request);

    ApiResponse<List<EmployeeSummaryItemDTO>> getEmployeeSummaryBatch(EmployeeBatchRequest request);

}
