package com.atlas_erp.payroll_service.DTO.Response;

import lombok.Data;

@Data
public class PayrollExportResponse {

    private byte[] file;
    private String fileName;
}
