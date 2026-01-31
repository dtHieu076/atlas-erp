package com.atlas_erp.payroll_service.Dto.Response.Payroll;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ApprovePayrollResponse {

    private Boolean success;
    private String payrollId;
    private String status;
    private LocalDateTime approvedAt;
    private String approvedBy;
}
