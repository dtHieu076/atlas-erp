package com.atlas_erp.inventory_service.DTO.Request;

import lombok.Data;

@Data
public class InventoryAlertQueryRequest {
    private String warehouseId;
    private String alertType; // LOW_STOCK | OVER_STOCK | AGING
}
