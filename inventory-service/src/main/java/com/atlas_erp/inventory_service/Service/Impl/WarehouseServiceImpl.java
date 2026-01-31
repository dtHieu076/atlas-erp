package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Dto.Request.Warehouse.WarehouseListRequest;
import com.atlas_erp.inventory_service.Dto.Response.Warehouse.WarehouseListResponse;
// import com.atlas_erp.inventory_service.Entity.WarehouseEntity;
// import com.atlas_erp.inventory_service.Repository.WarehouseRepository;
import com.atlas_erp.inventory_service.Service.WarehouseService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class WarehouseServiceImpl implements WarehouseService {

    // private final WarehouseRepository warehouseRepository;

    @Override
    public WarehouseListResponse findWarehousesByPlant(WarehouseListRequest request) {

        WarehouseListResponse response = new WarehouseListResponse();
        response.setList(List.of()); // list kho rỗng
        response.setTotal(0L);

        return response;
    }

}
