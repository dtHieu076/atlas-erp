package com.atlas_erp.inventory_service.Controller;

import com.atlas_erp.inventory_service.Dto.Request.Item.ItemListRequest;
import com.atlas_erp.inventory_service.Dto.Response.Item.ItemListResponse;
import com.atlas_erp.inventory_service.Service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    @GetMapping
    public ItemListResponse searchItems(ItemListRequest request) {
        return itemService.searchItems(request);
    }
}
