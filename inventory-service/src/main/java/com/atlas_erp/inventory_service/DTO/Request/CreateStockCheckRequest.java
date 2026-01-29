package com.atlas_erp.inventory_service.DTO.Request;

import lombok.Data;
import java.util.Date;
import java.util.List;
import java.math.BigDecimal;

@Data
public class CreateStockCheckRequest {

    private String warehouseId;
    private Date checkDate;
    private String createdBy;
    private List<StockCheckItem> items;

    @Data
    public static class StockCheckItem {
        private String itemId;
        private BigDecimal systemQty;
        private BigDecimal actualQty;
        private String note;
    }
}
