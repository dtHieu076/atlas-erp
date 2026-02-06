package com.atlas_erp.hr_service.Dto.Response.Attendance;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceImportResponse {

    private Boolean success;
    private Integer totalRecords;
    private Integer errorRecords;
}
