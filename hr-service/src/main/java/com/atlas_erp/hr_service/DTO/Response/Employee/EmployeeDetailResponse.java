package com.atlas_erp.hr_service.Dto.Response.Employee;

import lombok.Data;
import java.time.LocalDate;

// API: GET /employees/{id}
@Data
public class EmployeeDetailResponse {

    private String employeeId;
    private String employeeCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String phone;
    private String email;
    private String status;
    private LocalDate hiredDate;
    private String currentDepartmentId;
    private String currentTeamId;
}
