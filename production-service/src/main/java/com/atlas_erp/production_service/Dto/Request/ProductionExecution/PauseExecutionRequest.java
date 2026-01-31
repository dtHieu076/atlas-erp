package com.atlas_erp.production_service.Dto.Request.ProductionExecution;

import lombok.Data;

@Data
public class PauseExecutionRequest {
    private String executionId;
    private String reason;
}
