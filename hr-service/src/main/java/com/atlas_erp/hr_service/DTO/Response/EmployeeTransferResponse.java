package com.atlas_erp.hr_service.DTO.Response;

import lombok.Data;
import java.time.LocalDate;

@Data
public class EmployeeTransferResponse {

    private Boolean success;
    private String employeeId;
    private String transferId;
    private String status;
    private LocalDate effectiveDate;
}
