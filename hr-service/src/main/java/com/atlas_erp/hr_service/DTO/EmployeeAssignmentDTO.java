package com.atlas_erp.hr_service.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeAssignmentDTO {

    private String departmentId;
    private String teamId;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
}
