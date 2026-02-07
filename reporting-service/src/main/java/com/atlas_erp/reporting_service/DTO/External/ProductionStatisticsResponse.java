package com.atlas_erp.reporting_service.Dto.External;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class ProductionStatisticsResponse {
    private Integer totalPlanQty;
    private Integer totalActualQty;
    private Integer totalDefectQty;
    private Integer totalOrders;
    private Integer onTimeOrders;
    private BigDecimal capacityUtilization; // Tỷ lệ khai thác công suất
}