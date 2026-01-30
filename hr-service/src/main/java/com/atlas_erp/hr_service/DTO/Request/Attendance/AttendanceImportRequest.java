package com.atlas_erp.hr_service.Dto.Request.Attendance;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

// API: POST /attendance/import
@Data
public class AttendanceImportRequest {

    private MultipartFile file;
}
