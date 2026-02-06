package com.atlas_erp.hr_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class EmployeeMovedEvent {
    private UUID employeeId;
    private UUID fromDepartmentId;
    private UUID toDepartmentId;
    private LocalDate effectiveDate;
}