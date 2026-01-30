package com.atlas_erp.hr_service.Dto.Request.LeaveRequest;

import lombok.Data;
import java.time.LocalDate;

// API: POST /leave-requests
@Data
public class CreateLeaveRequest {

    private String employeeId;
    private String leaveType;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;
}
