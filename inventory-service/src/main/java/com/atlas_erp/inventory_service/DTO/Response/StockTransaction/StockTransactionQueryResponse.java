package com.atlas_erp.inventory_service.Dto.Response.StockTransaction;

import lombok.Data;
import java.util.List;
import java.util.Date;
import java.math.BigDecimal;

@Data
public class StockTransactionQueryResponse {

    private List<StockTransactionDto> transactions;

    @Data
    public static class StockTransactionDto {
        private String transactionId;
        private String transactionType; // IN | OUT | ADJUST
        private String itemId;
        private String batchId;
        private String warehouseId;
        private BigDecimal quantity;
        private String unit;
        private String referenceDocumentId;
        private Date createdAt;
    }
}
