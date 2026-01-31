package com.atlas_erp.inventory_service.Controller;

import com.atlas_erp.inventory_service.Dto.Request.InventoryAlert.InventoryAlertQueryRequest;
import com.atlas_erp.inventory_service.Dto.Response.InventoryAlert.InventoryAlertResponse;
import com.atlas_erp.inventory_service.Service.InventoryAlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/inventory-alerts")
@RequiredArgsConstructor
public class InventoryAlertController {

    private final InventoryAlertService inventoryAlertService;

    @GetMapping
    public InventoryAlertResponse getInventoryAlerts(
            InventoryAlertQueryRequest request) {
        return inventoryAlertService.getInventoryAlerts(request);
    }
}
