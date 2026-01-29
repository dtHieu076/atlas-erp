package com.atlas_erp.payroll_service.DTO;

import lombok.Data;
import java.time.LocalDate;

@Data
public class PayrollPeriodItemDTO {

    private String id;
    private Integer month;
    private Integer year;
    private LocalDate startDate;
    private LocalDate endDate;
    private String status;
}
