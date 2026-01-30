package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Dto.Request.Employee.*;
import com.atlas_erp.hr_service.Dto.Response.Employee.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.atlas_erp.hr_service.Service.EmployeeService;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    // private final EmployeeRepository employeeRepository;
    // private final EmployeeAssignmentRepository assignmentRepository;

    @Override
    public EmployeeListResponse getEmployees(EmployeeListRequest request) {

        EmployeeListResponse response = new EmployeeListResponse();
        response.setList(List.of()); // danh sách rỗng (stub)
        response.setTotal(0L);

        return response;
    }

    @Override
    public CreateEmployeeResponse createEmployee(CreateEmployeeRequest request) {

        CreateEmployeeResponse response = new CreateEmployeeResponse();
        response.setId("employee-id"); // id mẫu / placeholder

        return response;
    }

    @Override
    public UpdateEmployeeResponse updateEmployee(UUID id, UpdateEmployeeRequest request) {
        return UpdateEmployeeResponse.success();
    }

    @Override
    public EmployeeDetailResponse getEmployeeDetail(UUID employeeId) {

        EmployeeDetailResponse response = new EmployeeDetailResponse();

        response.setEmployeeId(employeeId.toString());
        response.setEmployeeCode("EMP001");
        response.setFullName("Nguyen Van A");
        response.setDateOfBirth(LocalDate.of(1995, 1, 1));
        response.setGender("MALE");
        response.setPhone("0900000000");
        response.setEmail("employee@example.com");
        response.setStatus("ACTIVE");
        response.setHiredDate(LocalDate.of(2022, 1, 1));
        response.setCurrentDepartmentId("DEPT001");
        response.setCurrentTeamId("TEAM001");

        return response;
    }

    @Override
    public EmployeeAssignmentHistoryResponse getAssignmentHistory(UUID employeeId) {

        EmployeeAssignmentHistoryResponse response = new EmployeeAssignmentHistoryResponse();
        response.setEmployeeId(employeeId.toString());
        response.setAssignments(List.of()); // danh sách rỗng

        return response;
    }

    @Override
    public EmployeeTransferResponse transferEmployee(
            UUID employeeId,
            EmployeeTransferRequest request) {

        EmployeeTransferResponse response = new EmployeeTransferResponse();
        response.setSuccess(true);
        response.setEmployeeId(employeeId.toString());
        response.setTransferId("transfer-id"); // placeholder
        response.setStatus("PENDING");
        response.setEffectiveDate(request.getEffectiveDate());

        return response;
    }

}
