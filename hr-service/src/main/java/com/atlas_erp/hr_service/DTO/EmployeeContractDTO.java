package com.atlas_erp.hr_service.DTO;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class EmployeeContractDTO {

    private String id;
    private String employeeId;
    private String contractType;
    private LocalDate startDate;
    private LocalDate endDate;
    private BigDecimal salary;
    private String status;
}
