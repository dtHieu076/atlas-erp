package com.atlas_erp.reporting_service.Dto.Request;

import lombok.Data;

@Data
public class InventoryDashboardRequest {

    private String warehouseId;
    private String period;
}
