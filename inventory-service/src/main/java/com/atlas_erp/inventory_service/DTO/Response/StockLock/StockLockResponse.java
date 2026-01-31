package com.atlas_erp.inventory_service.Dto.Response.StockLock;

import lombok.Data;
import lombok.AllArgsConstructor;

@AllArgsConstructor
@Data
public class StockLockResponse {
    private Boolean success;

    public static StockLockResponse success() {
        return new StockLockResponse(true);
    }

    public static StockLockResponse fail() {
        return new StockLockResponse(false);
    }
}
