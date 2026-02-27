package com.atlas_erp.hr_service.Dto.Response.Employee_v0;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class EmployeeSummaryItemDTO {

    private UUID id;
    private String employeeCode;
    private String fullName;
    private String email;
    private UUID departmentId;
}
