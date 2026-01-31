package com.atlas_erp.inventory_service.Dto.Response.Item;

import lombok.Data;
import java.util.List;

import com.atlas_erp.inventory_service.Dto.ItemDto;

@Data
public class ItemListResponse {
    private List<ItemDto> list;
    private Long total;
}
