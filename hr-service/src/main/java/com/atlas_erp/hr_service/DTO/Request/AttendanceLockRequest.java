package com.atlas_erp.hr_service.DTO.Request;

import lombok.Data;

// API: POST /attendance/lock
@Data
public class AttendanceLockRequest {

    private Integer month;
    private Integer year;
}
