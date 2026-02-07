package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Dto.Request.StockTransaction.InventoryStatisticsSummaryRequest;
import com.atlas_erp.inventory_service.Dto.Request.StockTransaction.StockTransactionQueryRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockTransaction.InventoryStatisticsSummaryResponse;
import com.atlas_erp.inventory_service.Dto.Response.StockTransaction.StockTransactionQueryResponse;
import com.atlas_erp.inventory_service.Repository.StockTransactionRepository;
import com.atlas_erp.inventory_service.Service.StockTransactionService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import java.util.UUID;
import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockTransactionServiceImpl implements StockTransactionService {

    private final StockTransactionRepository stockTransactionRepository;

    @Override // Chưa cần gấp
    public StockTransactionQueryResponse queryStockTransactions(
            StockTransactionQueryRequest request) {

        StockTransactionQueryResponse response = new StockTransactionQueryResponse();
        response.setTransactions(List.of()); // list rỗng

        return response;
    }

    @Override
    public InventoryStatisticsSummaryResponse getInventoryStatisticsSummary(
            InventoryStatisticsSummaryRequest request) {

        UUID warehouseId = UUID.fromString(request.getWarehouseId());

        log.info("Inventory statistics summary: warehouseId={}, period={}",
                request.getWarehouseId(), request.getPeriod());

        Integer totalItems = stockTransactionRepository.countDistinctItems(warehouseId);

        BigDecimal totalQuantity = stockTransactionRepository.sumQuantity(warehouseId);

        InventoryStatisticsSummaryResponse response = new InventoryStatisticsSummaryResponse();

        response.setTotalItems(totalItems != null ? totalItems : 0);
        response.setTotalQuantity(totalQuantity != null ? totalQuantity : BigDecimal.ZERO);

        // Stub – refine bằng policy / batch sau
        response.setLowStockCount(0);
        response.setOverStockCount(0);
        response.setAgingStockCount(0);

        return response;
    }
}
