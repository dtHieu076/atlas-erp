package com.atlas_erp.hr_service.Dto.Request.Attendance;

import lombok.Data;
import java.time.LocalDate;

// API: POST /attendance/adjust
@Data
public class AttendanceAdjustRequest {

    private String employeeId;
    private LocalDate date;
    private String adjustmentType; // CHECK_IN / CHECK_OUT / OVERTIME
    private String oldValue;
    private String newValue;
    private String reason;
}
