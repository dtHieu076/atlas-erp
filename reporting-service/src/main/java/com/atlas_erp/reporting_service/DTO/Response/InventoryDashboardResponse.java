package com.atlas_erp.reporting_service.DTO.Response;

import lombok.Data;

@Data
public class InventoryDashboardResponse {

    private String warehouseId;
    private Integer totalItems;
    private Double totalStockQuantity;
    private Integer lowStockCount;
    private Integer overStockCount;
    private Integer agingStockCount;
}
