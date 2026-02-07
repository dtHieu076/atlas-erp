package com.atlas_erp.hr_service.Dto.Request.Employee;

import lombok.Data;

@Data
public class EmployeeStatisticsRequest {
    private String period; // Định dạng: yyyy-MM
    private String departmentId; // Có thể null để lấy toàn công ty
}