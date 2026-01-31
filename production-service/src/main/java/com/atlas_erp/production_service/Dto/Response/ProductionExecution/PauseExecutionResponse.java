package com.atlas_erp.production_service.Dto.Response.ProductionExecution;

import lombok.Data;
import java.util.Date;

@Data
public class PauseExecutionResponse {

    private Boolean success;
    private String executionId;
    private String status;
    private Date pausedAt;
}
