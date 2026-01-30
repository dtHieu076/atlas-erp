package com.atlas_erp.hr_service.Dto.Request.Employee;

import lombok.Data;
import java.time.LocalDate;

// API: POST /employees
@Data
public class CreateEmployeeRequest {

    private String employeeCode;
    private String fullName;
    private String gender;
    private LocalDate birthDate;
    private String phone;
    private String email;
    private String organizationId;
    private String plantId;
    private String departmentId;
    private String teamId;
    private String position;
    private LocalDate joinDate;
    private String status;
}
