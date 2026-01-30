package com.atlas_erp.hr_service.Dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeItemDTO {

    private String id;
    private String employeeCode;
    private String fullName;
    private String gender;
    private LocalDate birthDate;
    private String phone;
    private String email;
    private String departmentId;
    private String teamId;
    private String position;
    private String status;
}
