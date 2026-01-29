package com.atlas_erp.inventory_service.DTO.Request;

import lombok.Data;

@Data
public class WarehouseListRequest {
    private String plantId;
    private Integer page;
    private Integer pageSize;
}
