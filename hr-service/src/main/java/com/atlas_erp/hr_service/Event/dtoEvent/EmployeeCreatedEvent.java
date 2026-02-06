package com.atlas_erp.hr_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;
import java.time.LocalDate;

@Data
@Builder
public class EmployeeCreatedEvent {
    private UUID employeeId;
    private String employeeCode;
    private String email;
    private UUID departmentId;
    private UUID teamId;
    private LocalDate joinDate;
}
