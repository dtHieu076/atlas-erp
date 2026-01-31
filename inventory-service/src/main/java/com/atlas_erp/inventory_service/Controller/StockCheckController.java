package com.atlas_erp.inventory_service.Controller;

import com.atlas_erp.inventory_service.Dto.Request.StockCheck.CreateStockCheckRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockCheck.CreateStockCheckResponse;
import com.atlas_erp.inventory_service.Service.StockCheckService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock-checks")
@RequiredArgsConstructor
public class StockCheckController {

    private final StockCheckService stockCheckService;

    @PostMapping
    public CreateStockCheckResponse createStockCheck(
            @RequestBody CreateStockCheckRequest request) {
        return stockCheckService.createStockCheck(request);
    }
}
