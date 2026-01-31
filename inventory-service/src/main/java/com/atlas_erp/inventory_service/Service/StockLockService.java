package com.atlas_erp.inventory_service.Service;

import com.atlas_erp.inventory_service.Dto.Request.StockLock.StockLockRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockLock.StockLockResponse;

public interface StockLockService {

    /**
     * Khóa kho theo kỳ kế toán
     */
    StockLockResponse lockInventoryPeriod(StockLockRequest request);
}
