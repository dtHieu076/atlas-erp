package com.atlas_erp.payroll_service.DTO.Response;

import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class PayrollCalculateResponse {

    private String periodId;
    private Integer totalEmployee;
    private BigDecimal totalAmount;
    private LocalDateTime calculatedAt;
    private String status;
}
