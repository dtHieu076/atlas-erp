package com.atlas_erp.hr_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class EmployeeUpdatedEvent {
    private UUID employeeId;
    private String fullName;
    private String email;
    private String status;
    private UUID departmentId;
}
