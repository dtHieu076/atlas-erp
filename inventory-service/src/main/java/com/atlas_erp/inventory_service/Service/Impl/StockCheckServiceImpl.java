package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Dto.Request.StockCheck.CreateStockCheckRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockCheck.CreateStockCheckResponse;
import com.atlas_erp.inventory_service.Service.StockCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Transactional
public class StockCheckServiceImpl implements StockCheckService {

    @Override
    public CreateStockCheckResponse createStockCheck(CreateStockCheckRequest request) {

        CreateStockCheckResponse response = new CreateStockCheckResponse();
        response.setId(UUID.randomUUID().toString()); // id giả

        return response;
    }

}
