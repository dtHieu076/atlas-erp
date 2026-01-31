package com.atlas_erp.inventory_service.Service;

import java.util.UUID;

import com.atlas_erp.inventory_service.Dto.Response.BatchTrace.BatchTraceResponse;

public interface BatchTraceService {

    /**
     * Truy xuất nguồn gốc lô hàng
     */
    BatchTraceResponse traceBatchHistory(UUID batchId);
}
