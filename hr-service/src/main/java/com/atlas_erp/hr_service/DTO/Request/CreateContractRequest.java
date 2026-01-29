package com.atlas_erp.hr_service.DTO.Request;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

// API: POST /contracts
@Data
public class CreateContractRequest {

    private String employeeId;
    private String contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal salary;
    private Integer workingHours;
    private String status;
}
