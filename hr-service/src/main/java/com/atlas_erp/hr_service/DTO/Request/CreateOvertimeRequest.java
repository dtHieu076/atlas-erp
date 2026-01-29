package com.atlas_erp.hr_service.DTO.Request;

import lombok.Data;
import java.time.LocalDate;
import java.time.LocalTime;

// API: POST /overtime-requests
@Data
public class CreateOvertimeRequest {

    private String employeeId;
    private LocalDate otDate;
    private LocalTime fromTime;
    private LocalTime toTime;
    private String reason;
}
