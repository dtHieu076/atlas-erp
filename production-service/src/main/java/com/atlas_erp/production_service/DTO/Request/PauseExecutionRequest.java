package com.atlas_erp.production_service.DTO.Request;

import lombok.Data;

@Data
public class PauseExecutionRequest {
    private String executionId;
    private String reason;
}
