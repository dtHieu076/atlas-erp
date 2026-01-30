package com.atlas_erp.hr_service.Dto.Response.Attendance;

import lombok.Data;

@Data
public class AttendanceImportResponse {

    private Boolean success;
    private Integer totalRecords;
    private Integer errorRecords;
}
