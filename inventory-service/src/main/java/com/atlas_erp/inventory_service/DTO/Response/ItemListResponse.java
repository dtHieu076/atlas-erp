package com.atlas_erp.inventory_service.DTO.Response;

import lombok.Data;
import java.util.List;
import com.atlas_erp.inventory_service.DTO.ItemDto;

@Data
public class ItemListResponse {
    private List<ItemDto> list;
    private Long total;
}
