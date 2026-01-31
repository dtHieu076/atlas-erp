package com.atlas_erp.inventory_service.Service;

import com.atlas_erp.inventory_service.Dto.Request.Warehouse.WarehouseListRequest;
import com.atlas_erp.inventory_service.Dto.Response.Warehouse.WarehouseListResponse;

public interface WarehouseService {

    /**
     * Tra cứu danh sách kho theo nhà máy
     */
    WarehouseListResponse findWarehousesByPlant(WarehouseListRequest request);
}
