package com.atlas_erp.hr_service.DTO.Request;

import lombok.Data;

// API: PUT /employees/{id}
@Data
public class UpdateEmployeeRequest {

    private String fullName;
    private String phone;
    private String email;
    private String departmentId;
    private String teamId;
    private String position;
    private String status;
}
