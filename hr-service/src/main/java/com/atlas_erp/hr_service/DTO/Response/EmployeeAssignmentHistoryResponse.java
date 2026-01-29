package com.atlas_erp.hr_service.DTO.Response;

import com.atlas_erp.hr_service.DTO.EmployeeAssignmentDTO;
import lombok.Data;
import java.util.List;

// API: GET /employees/{id}/assignments
@Data
public class EmployeeAssignmentHistoryResponse {

    private String employeeId;
    private List<EmployeeAssignmentDTO> assignments;
}
