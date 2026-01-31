package com.atlas_erp.inventory_service.Dto.Request.InventoryAlert;

import lombok.Data;

@Data
public class InventoryAlertQueryRequest {
    private String warehouseId;
    private String alertType; // LOW_STOCK | OVER_STOCK | AGING
}
