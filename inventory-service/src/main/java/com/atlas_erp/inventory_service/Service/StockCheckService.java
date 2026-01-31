package com.atlas_erp.inventory_service.Service;

import com.atlas_erp.inventory_service.Dto.Request.StockCheck.CreateStockCheckRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockCheck.CreateStockCheckResponse;

public interface StockCheckService {

    /**
     * Tạo phiếu kiểm kê kho
     */
    CreateStockCheckResponse createStockCheck(CreateStockCheckRequest request);
}
