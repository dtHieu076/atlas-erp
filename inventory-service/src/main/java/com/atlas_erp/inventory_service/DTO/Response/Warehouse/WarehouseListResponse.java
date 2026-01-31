package com.atlas_erp.inventory_service.Dto.Response.Warehouse;

import lombok.Data;
import java.util.List;

import com.atlas_erp.inventory_service.Dto.WarehouseDto;

@Data
public class WarehouseListResponse {
    private List<WarehouseDto> list;
    private Long total;
}
