package com.atlas_erp.payroll_service.DTO.Response;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class RejectPayrollResponse {

    private Boolean success;
    private String payrollId;
    private String status;
    private LocalDateTime rejectedAt;
    private String rejectedBy;
}
