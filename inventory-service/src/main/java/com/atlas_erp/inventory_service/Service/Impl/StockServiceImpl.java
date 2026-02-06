package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Cache.serviceCache.InventoryCacheService;
import com.atlas_erp.inventory_service.Dto.Request.StockQuery.StockQueryRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockQuery.StockQueryResponse;
import com.atlas_erp.inventory_service.Repository.StockTransactionRepository;
import com.atlas_erp.inventory_service.Service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockServiceImpl implements StockService {

    private final StockTransactionRepository stockTransactionRepository;
    private final InventoryCacheService inventoryCacheService;

    @Override
    public StockQueryResponse getCurrentStockBalance(StockQueryRequest request) {
        String warehouseId = request.getWarehouseId();
        log.info("Truy vấn tồn kho cho kho: {}", warehouseId);

        // 1. Kiểm tra Cache trước
        StockQueryResponse cachedData = inventoryCacheService.getStockBalanceCache(warehouseId);
        if (cachedData != null) {
            log.info("Cache Hit - Lấy tồn kho từ Redis cho kho: {}", warehouseId);
            return cachedData;
        }

        // 2. Cache Miss - Tính toán từ Database (StockTransactionEntity)
        log.info("Cache Miss - Đang tính toán tồn kho từ Transaction Ledger...");
        UUID warehouseUuid = UUID.fromString(warehouseId);

        // Query SUM(quantity) GROUP BY itemId từ Repository bạn đã tạo
        List<StockQueryResponse.StockBalanceDto> balances = stockTransactionRepository
                .calculateCurrentStockByWarehouse(warehouseUuid);

        StockQueryResponse response = new StockQueryResponse();
        response.setStock(balances);

        // 3. Lưu vào Cache để dùng cho lần sau
        inventoryCacheService.putStockBalanceCache(warehouseId, response);

        return response;
    }
}