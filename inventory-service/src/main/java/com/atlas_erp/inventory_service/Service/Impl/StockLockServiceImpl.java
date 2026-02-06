package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Dto.Request.StockLock.StockLockRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockLock.StockLockResponse;
import com.atlas_erp.inventory_service.Service.StockLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Transactional
public class StockLockServiceImpl implements StockLockService {

    @Override // Chưa cần gấp
    public StockLockResponse lockInventoryPeriod(StockLockRequest request) {

        // - Kiểm tra kỳ đã khóa chưa
        // - Ghi nhận inventory_lock

        return new StockLockResponse(true);
    }
}
