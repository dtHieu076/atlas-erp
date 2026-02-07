package com.atlas_erp.inventory_service.Service;

import com.atlas_erp.inventory_service.Dto.Request.StockTransaction.InventoryStatisticsSummaryRequest;
import com.atlas_erp.inventory_service.Dto.Request.StockTransaction.StockTransactionQueryRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockTransaction.InventoryStatisticsSummaryResponse;
import com.atlas_erp.inventory_service.Dto.Response.StockTransaction.StockTransactionQueryResponse;

public interface StockTransactionService {

    /**
     * Tra cứu lịch sử giao dịch nhập – xuất – điều chỉnh kho
     */
    StockTransactionQueryResponse queryStockTransactions(StockTransactionQueryRequest request);

    /**
     * Thống kê tổng hợp tồn kho theo kho & kỳ
     */
    InventoryStatisticsSummaryResponse getInventoryStatisticsSummary(
            InventoryStatisticsSummaryRequest request);
}
