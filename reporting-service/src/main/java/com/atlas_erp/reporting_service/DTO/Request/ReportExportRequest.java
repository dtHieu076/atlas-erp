package com.atlas_erp.reporting_service.Dto.Request;

import lombok.Data;

@Data
public class ReportExportRequest {

    private String reportType;
    private String periodType;
    private String periodValue;
    private String format; // xlsx | pdf | csv
}
