package com.atlas_erp.hr_service.DTO.Request;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

// API: POST /attendance/import
@Data
public class AttendanceImportRequest {

    private MultipartFile file;
}
