package com.atlas_erp.inventory_service.Dto.Request.Warehouse;

import lombok.Data;

@Data
public class WarehouseListRequest {
    private String plantId;
    private Integer page;
    private Integer pageSize;
}
