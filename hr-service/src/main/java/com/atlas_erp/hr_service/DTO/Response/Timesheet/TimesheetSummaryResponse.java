package com.atlas_erp.hr_service.Dto.Response.Timesheet;

import lombok.Data;
import java.util.UUID;

@Data
public class TimesheetSummaryResponse {
    private UUID employeeId;
    private String employeeCode;
    private String fullName;
    private UUID departmentId;
    private String departmentName;
    private String avatarUrl;

    private String period;

    private Integer standardDays;
    private Integer actualDays;
    private Integer paidLeaveDays;
    private Integer unpaidLeaveDays;
    private Double otHours;
    private Integer lateMinutes;

    private Boolean isLocked;
}
