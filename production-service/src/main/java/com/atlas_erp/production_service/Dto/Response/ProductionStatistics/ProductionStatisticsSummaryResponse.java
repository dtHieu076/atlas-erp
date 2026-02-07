package com.atlas_erp.production_service.Dto.Response.ProductionStatistics;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductionStatisticsSummaryResponse {

    private Integer totalPlanQty;
    private Integer totalActualQty;
    private Integer totalDefectQty;

    private Integer totalOrders;
    private Integer onTimeOrders;

    /**
     * 0 -> 1 (hoặc *100 hiển thị %)
     */
    private BigDecimal capacityUtilization;
}
