package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Dto.Request.InventoryAlert.InventoryAlertQueryRequest;
import com.atlas_erp.inventory_service.Dto.Response.InventoryAlert.InventoryAlertResponse;
import com.atlas_erp.inventory_service.Service.InventoryAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class InventoryAlertServiceImpl implements InventoryAlertService {

    @Override
    public InventoryAlertResponse getInventoryAlerts(
            InventoryAlertQueryRequest request) {

        // TODO:
        // LOW_STOCK / OVER_STOCK / AGING

        return new InventoryAlertResponse();
    }
}
