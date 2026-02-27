package com.atlas_erp.hr_service.Dto.Response.Employee;

import com.atlas_erp.hr_service.enums.*;
import lombok.*;
import lombok.experimental.SuperBuilder;

import java.time.LocalDate;
import java.util.UUID;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeResponse {

    private UUID id;
    private String employeeCode;
    private String fullName;
    private String avatarUrl;

    private GenderType gender;

    private UUID departmentId;
    private String departmentName;

    private UUID teamId;
    private String teamName;

    private UUID jobTitleId;
    private String jobTitleName;

    private String gradeLevel;

    private LocalDate hireDate;
    private String employmentType;

    private EmploymentStatus status;

    private String phone;
    private String workEmail;
}