package com.atlas_erp.hr_service.Dto.Request.Employee;

import lombok.Data;
import java.time.LocalDate;

// API: POST /employees/{id}/transfer
@Data
public class EmployeeTransferRequest {

    private String fromDepartmentId;
    private String fromTeamId;
    private String toDepartmentId;
    private String toTeamId;
    private LocalDate effectiveDate;
    private String reason;
}
