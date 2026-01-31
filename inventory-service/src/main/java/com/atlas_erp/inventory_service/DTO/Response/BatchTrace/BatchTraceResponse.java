package com.atlas_erp.inventory_service.Dto.Response.BatchTrace;

import lombok.Data;
import java.util.List;
import java.util.Date;
import java.math.BigDecimal;

@Data
public class BatchTraceResponse {

    private String batchId;
    private String itemId;
    private List<TraceStep> traceSteps;

    @Data
    public static class TraceStep {
        private String action; // RECEIVED | ISSUED | USED
        private String referenceId;
        private String warehouseId;
        private BigDecimal quantity;
        private Date occurredAt;
    }
}
