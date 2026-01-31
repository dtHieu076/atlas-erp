package com.atlas_erp.inventory_service.Service;


import com.atlas_erp.inventory_service.Dto.Request.InventoryAlert.InventoryAlertQueryRequest;
import com.atlas_erp.inventory_service.Dto.Response.InventoryAlert.InventoryAlertResponse;


public interface InventoryAlertService {

    /**
     * Lấy danh sách cảnh báo tồn kho
     */
    InventoryAlertResponse getInventoryAlerts(InventoryAlertQueryRequest request);
}
