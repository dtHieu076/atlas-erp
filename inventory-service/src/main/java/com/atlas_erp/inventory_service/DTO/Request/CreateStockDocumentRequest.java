package com.atlas_erp.inventory_service.DTO.Request;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class CreateStockDocumentRequest {

    private String docType; // IMPORT | EXPORT | TRANSFER
    private String warehouseId;
    private String relatedDeptId;
    private String createdBy;
    private String note;
    private List<StockItemRequest> items;

    @Data
    public static class StockItemRequest {
        private String itemId;
        private BigDecimal quantity;
        private BigDecimal unitPrice;
    }
}
