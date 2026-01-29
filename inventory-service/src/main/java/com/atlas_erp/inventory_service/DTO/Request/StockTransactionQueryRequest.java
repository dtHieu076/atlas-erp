package com.atlas_erp.inventory_service.DTO.Request;

import lombok.Data;
import java.util.Date;

@Data
public class StockTransactionQueryRequest {
    private Date fromDate;
    private Date toDate;
    private String warehouseId;
    private String itemId;
    private String batchId;
}
