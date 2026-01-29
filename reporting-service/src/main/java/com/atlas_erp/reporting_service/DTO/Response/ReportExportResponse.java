package com.atlas_erp.reporting_service.DTO.Response;

import lombok.Data;

@Data
public class ReportExportResponse {

    private byte[] file;
    private String fileName;
}
