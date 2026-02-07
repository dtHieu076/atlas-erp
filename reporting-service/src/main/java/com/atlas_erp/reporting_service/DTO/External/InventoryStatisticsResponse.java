package com.atlas_erp.reporting_service.Dto.External;

import lombok.Data;
import java.math.BigDecimal;

@Data
public class InventoryStatisticsResponse {
    private Integer totalItems;
    private BigDecimal totalStockQuantity;
    private Integer lowStockCount;
    private Integer overStockCount;
    private Integer agingStockCount;
}