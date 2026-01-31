package com.atlas_erp.inventory_service.Dto.Response.StockQuery;

import lombok.Data;
import java.util.List;
import java.math.BigDecimal;

@Data
public class StockQueryResponse {
    private List<StockBalanceDto> stock;

    @Data
    public static class StockBalanceDto {
        private String itemId;
        private String itemCode;
        private String itemName;
        private BigDecimal quantity;
        private String unit;
    }
}
