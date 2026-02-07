package com.atlas_erp.production_service.Dto.Request.ProductionStatistics;

import lombok.Data;

@Data
public class ProductionStatisticsSummaryRequest {

    /**
     * DAY | MONTH | YEAR
     */
    private String periodType;

    /**
     * Ví dụ:
     * - DAY : 2026-02-01
     * - MONTH : 2026-02
     * - YEAR : 2026
     */
    private String periodValue;

    /**
     * Nullable – nếu theo plant
     */
    private String plantId;
}
