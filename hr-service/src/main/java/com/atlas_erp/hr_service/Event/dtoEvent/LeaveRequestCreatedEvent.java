package com.atlas_erp.hr_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class LeaveRequestCreatedEvent {
    private UUID leaveRequestId;
    private UUID employeeId;
    private String leaveType; // Ví dụ: ANNUAL_LEAVE, SICK_LEAVE, UNPAID_LEAVE
    private LocalDate fromDate;
    private LocalDate toDate;
    private String workflowId;
    private String reason;
}
