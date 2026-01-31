package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Dto.Request.StockTransaction.StockTransactionQueryRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockTransaction.StockTransactionQueryResponse;
// import com.atlas_erp.inventory_service.Entity.StockTransactionEntity;
// import com.atlas_erp.inventory_service.Repository.StockTransactionRepository;
import com.atlas_erp.inventory_service.Service.StockTransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockTransactionServiceImpl implements StockTransactionService {

    // private final StockTransactionRepository stockTransactionRepository;

    @Override
    public StockTransactionQueryResponse queryStockTransactions(
            StockTransactionQueryRequest request) {

        StockTransactionQueryResponse response = new StockTransactionQueryResponse();
        response.setTransactions(List.of()); // list rỗng

        return response;
    }

}
