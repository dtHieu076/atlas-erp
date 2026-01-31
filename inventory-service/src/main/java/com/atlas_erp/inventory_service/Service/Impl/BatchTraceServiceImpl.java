package com.atlas_erp.inventory_service.Service.Impl;

import com.atlas_erp.inventory_service.Dto.Response.BatchTrace.BatchTraceResponse;
import com.atlas_erp.inventory_service.Service.BatchTraceService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BatchTraceServiceImpl implements BatchTraceService {

    @Override
    public BatchTraceResponse traceBatchHistory(UUID batchId) {

        BatchTraceResponse response = new BatchTraceResponse();
        response.setBatchId(batchId.toString());
        response.setItemId(null); // chưa xác định
        response.setTraceSteps(List.of()); // list rỗng

        return response;
    }

}
