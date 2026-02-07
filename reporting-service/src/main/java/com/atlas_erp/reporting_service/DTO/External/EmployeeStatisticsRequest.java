package com.atlas_erp.reporting_service.Dto.External;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeStatisticsRequest {
    private String period; // yyyy-MM
    private String departmentId; // UUID String
}