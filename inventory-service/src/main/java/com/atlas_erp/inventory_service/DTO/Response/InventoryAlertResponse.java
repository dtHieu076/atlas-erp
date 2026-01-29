package com.atlas_erp.inventory_service.DTO.Response;

import lombok.Data;
import java.util.List;
import java.util.Date;
import java.math.BigDecimal;

@Data
public class InventoryAlertResponse {

    private List<InventoryAlertDto> alerts;

    @Data
    public static class InventoryAlertDto {
        private String alertId;
        private String alertType;
        private String warehouseId;
        private String itemId;
        private BigDecimal currentQuantity;
        private BigDecimal threshold;
        private Date generatedAt;
    }
}
