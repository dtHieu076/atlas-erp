package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Dto.Request.Item.ItemListRequest;
import com.atlas_erp.inventory_service.Dto.Response.Item.ItemListResponse;
import com.atlas_erp.inventory_service.Service.ItemService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ItemServiceImpl implements ItemService {

    // private final ItemRepository itemRepository;

    @Override
    public ItemListResponse searchItems(ItemListRequest request) {

        ItemListResponse response = new ItemListResponse();
        response.setList(List.of()); // list rỗng
        response.setTotal(0L);

        return response;
    }

}
