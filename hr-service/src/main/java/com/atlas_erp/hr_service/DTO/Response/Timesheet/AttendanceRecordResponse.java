package com.atlas_erp.hr_service.Dto.Response.Timesheet;

import lombok.Data;
import com.atlas_erp.hr_service.enums.AttendanceStatus;
import java.util.UUID;
import java.time.LocalDateTime;
import java.time.LocalDate;

@Data
public class AttendanceRecordResponse {
    private UUID id;
    private UUID employeeId;
    private LocalDate date;
    private UUID shiftId;
    private LocalDateTime checkIn;
    private LocalDateTime checkOut;

    private Integer workMinutes;
    private Integer otMinutes;
    private Integer lateMinutes;
    private Integer earlyLeaveMinutes;

    private String leaveType; // PAID / UNPAID

    private AttendanceStatus status;

    private String note;
}
