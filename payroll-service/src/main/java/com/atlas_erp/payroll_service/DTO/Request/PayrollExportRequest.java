package com.atlas_erp.payroll_service.DTO.Request;

import lombok.Data;

// API: GET /payroll/export
@Data
public class PayrollExportRequest {

    private String periodId;
    private String format; // xlsx | csv | pdf
}
