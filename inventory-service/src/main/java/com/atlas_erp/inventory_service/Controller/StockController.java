package com.atlas_erp.inventory_service.Controller;

import com.atlas_erp.inventory_service.Dto.Request.StockQuery.StockQueryRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockQuery.StockQueryResponse;
import com.atlas_erp.inventory_service.Service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock")
@RequiredArgsConstructor
public class StockController {

    private final StockService stockService;

    @GetMapping
    public StockQueryResponse getCurrentStockBalance(StockQueryRequest request) {
        return stockService.getCurrentStockBalance(request);
    }
}
