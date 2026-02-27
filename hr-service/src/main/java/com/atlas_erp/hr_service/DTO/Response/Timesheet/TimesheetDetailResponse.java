package com.atlas_erp.hr_service.Dto.Response.Timesheet;

import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
public class TimesheetDetailResponse {
    private UUID employeeId;
    private String period;
    private List<AttendanceRecordResponse> records;
}
