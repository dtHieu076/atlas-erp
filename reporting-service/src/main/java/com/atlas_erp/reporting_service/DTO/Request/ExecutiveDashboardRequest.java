package com.atlas_erp.reporting_service.Dto.Request;

import lombok.Data;

@Data
public class ExecutiveDashboardRequest {

    private String periodType; // DAY | MONTH | YEAR
    private String periodValue; // 2026-01 | 2026
}
