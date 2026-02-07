package com.atlas_erp.inventory_service.Dto.Request.StockTransaction;


import lombok.Data;

@Data
public class InventoryStatisticsSummaryRequest {

    /**
     * ID kho
     */
    private String warehouseId;

    /**
     * Kỳ thống kê (VD: 2025-01, 2025-Q1)
     */
    private String period;
}

