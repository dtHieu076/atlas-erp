package com.atlas_erp.inventory_service.Controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atlas_erp.inventory_service.Dto.Request.StockTransaction.StockTransactionQueryRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockTransaction.StockTransactionQueryResponse;
import com.atlas_erp.inventory_service.Service.StockTransactionService;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/stock-transactions")
@RequiredArgsConstructor
public class StockTransactionController {

    private final StockTransactionService stockTransactionService;

    @GetMapping
    public StockTransactionQueryResponse queryStockTransactions(StockTransactionQueryRequest request) {
        return stockTransactionService.queryStockTransactions(request);
    }
}
