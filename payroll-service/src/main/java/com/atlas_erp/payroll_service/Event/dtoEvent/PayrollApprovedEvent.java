package com.atlas_erp.payroll_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
public class PayrollApprovedEvent {
    private UUID payrollId;
    private UUID employeeId;
    private UUID periodId;
    private BigDecimal netSalary;
    private String approvedBy;
    private LocalDateTime approvedAt;
    private String status;
}