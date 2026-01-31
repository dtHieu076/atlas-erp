package com.atlas_erp.inventory_service.Service;

import com.atlas_erp.inventory_service.Dto.Request.Item.ItemListRequest;
import com.atlas_erp.inventory_service.Dto.Response.Item.ItemListResponse;

public interface ItemService {

    /**
     * Tìm kiếm danh mục vật tư theo điều kiện lọc
     */
    ItemListResponse searchItems(
            ItemListRequest request);
}
