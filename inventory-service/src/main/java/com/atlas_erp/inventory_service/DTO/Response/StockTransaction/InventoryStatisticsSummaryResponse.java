package com.atlas_erp.inventory_service.Dto.Response.StockTransaction;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class InventoryStatisticsSummaryResponse {

    private Integer totalItems;
    private BigDecimal totalQuantity;

    private Integer lowStockCount;
    private Integer overStockCount;
    private Integer agingStockCount;
}
