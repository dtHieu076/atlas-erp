package com.atlas_erp.inventory_service.Controller;

import com.atlas_erp.inventory_service.Dto.Request.Warehouse.WarehouseListRequest;
import com.atlas_erp.inventory_service.Dto.Response.Warehouse.WarehouseListResponse;
import com.atlas_erp.inventory_service.Service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/warehouses")
@RequiredArgsConstructor
public class WarehouseController {

    private final WarehouseService warehouseService;

    @GetMapping
    public WarehouseListResponse findWarehousesByPlant(WarehouseListRequest request) {
        return warehouseService.findWarehousesByPlant(request);
    }
}
