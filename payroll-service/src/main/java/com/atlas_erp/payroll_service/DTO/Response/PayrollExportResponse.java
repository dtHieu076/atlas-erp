package com.atlas_erp.payroll_service.Dto.Response;

import lombok.Data;

@Data
public class PayrollExportResponse {

    private byte[] file;
    private String fileName;
}
