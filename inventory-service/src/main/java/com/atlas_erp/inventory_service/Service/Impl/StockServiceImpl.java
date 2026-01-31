package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Dto.Request.StockQuery.StockQueryRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockQuery.StockQueryResponse;
import com.atlas_erp.inventory_service.Service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    @Override
    public StockQueryResponse getCurrentStockBalance(StockQueryRequest request) {

        StockQueryResponse response = new StockQueryResponse();
        response.setStock(List.of()); // tồn kho rỗng (stub)

        return response;
    }

}
