package com.atlas_erp.inventory_service.Controller;

import com.atlas_erp.inventory_service.Dto.Request.StockLock.StockLockRequest;
import com.atlas_erp.inventory_service.Dto.Response.StockLock.StockLockResponse;
import com.atlas_erp.inventory_service.Service.StockLockService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/stock-lock")
@RequiredArgsConstructor
public class StockLockController {

    private final StockLockService stockLockService;

    @PostMapping
    public StockLockResponse lockInventoryPeriod(@RequestBody StockLockRequest request) {
        return stockLockService.lockInventoryPeriod(request);
    }
}
