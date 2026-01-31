package com.atlas_erp.inventory_service.Dto.Request.StockLock;

import lombok.Data;

@Data
public class StockLockRequest {
    private Integer month;
    private Integer year;
    private String warehouseId;
}
