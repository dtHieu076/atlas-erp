package com.atlas_erp.inventory_service.Dto.Response.StockDocument;

import lombok.Data;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

@Data
public class StockDocumentDetailResponse {

    private String stockDocumentId;
    private String documentType; // RECEIPT | ISSUE | ADJUSTMENT
    private String warehouseId;
    private String status;
    private Date createdAt;
    private String createdBy;
    private List<StockDocumentItem> items;

    @Data
    public static class StockDocumentItem {
        private String itemId;
        private String batchId;
        private BigDecimal quantity;
        private String unit;
    }
}
