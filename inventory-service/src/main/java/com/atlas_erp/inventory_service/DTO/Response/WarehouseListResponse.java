package com.atlas_erp.inventory_service.DTO.Response;

import lombok.Data;
import java.util.List;
import com.atlas_erp.inventory_service.DTO.WarehouseDto;

@Data
public class WarehouseListResponse {
    private List<WarehouseDto> list;
    private Long total;
}
