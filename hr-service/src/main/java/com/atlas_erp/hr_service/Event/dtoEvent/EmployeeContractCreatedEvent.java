package com.atlas_erp.hr_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
public class EmployeeContractCreatedEvent {
    private UUID contractId;
    private UUID employeeId;
    private BigDecimal salary;
    private LocalDate startDate;
    private String contractType;
}