package com.atlas_erp.inventory_service.Service;

import com.atlas_erp.inventory_service.Dto.Request.StockQuery.StockQueryRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockQuery.StockQueryResponse;

public interface StockService {

    /**
     * Tra cứu tồn kho hiện tại theo kho
     */
    StockQueryResponse getCurrentStockBalance(StockQueryRequest request);
}
