package com.atlas_erp.reporting_service.DTO.Request;

import lombok.Data;

@Data
public class ExecutiveDashboardRequest {

    private String periodType; // DAY | MONTH | YEAR
    private String periodValue; // 2026-01 | 2026
}
