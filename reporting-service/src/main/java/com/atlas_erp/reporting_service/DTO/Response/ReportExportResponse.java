package com.atlas_erp.reporting_service.Dto.Response;

import lombok.Data;

@Data
public class ReportExportResponse {

    private byte[] file;
    private String fileName;
}
