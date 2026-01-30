package com.atlas_erp.hr_service.Dto.Request.Attendance;

import lombok.Data;
import java.time.LocalDate;

// API: GET /attendance
@Data
public class AttendanceListRequest {

    private LocalDate fromDate;
    private LocalDate toDate;
    private String employeeId;
    private String departmentId;
}
