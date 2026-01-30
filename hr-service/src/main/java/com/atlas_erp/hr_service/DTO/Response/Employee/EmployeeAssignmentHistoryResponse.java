package com.atlas_erp.hr_service.Dto.Response.Employee;

import lombok.Data;
import java.util.List;

import com.atlas_erp.hr_service.Dto.EmployeeAssignmentDTO;

// API: GET /employees/{id}/assignments
@Data
public class EmployeeAssignmentHistoryResponse {

    private String employeeId;
    private List<EmployeeAssignmentDTO> assignments;
}
