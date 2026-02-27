package com.atlas_erp.hr_service.Dto.Response.Employee;

import com.atlas_erp.hr_service.enums.*;
import lombok.*;
import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ContractResponse {

    private UUID id;
    private UUID contractTemplateId;
    private String contractTemplateName;
    private String contractNo;

    private LocalDate startDate;
    private LocalDate endDate;

    private java.math.BigDecimal baseSalary;

    private ContractStatus status;
}