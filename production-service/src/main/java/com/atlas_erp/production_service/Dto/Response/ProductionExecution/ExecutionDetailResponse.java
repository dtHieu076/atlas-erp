package com.atlas_erp.production_service.Dto.Response.ProductionExecution;

import lombok.Data;
import java.util.Date;

@Data
public class ExecutionDetailResponse {

    private String executionId;
    private String productionPlanId;
    private String status; // RUNNING | PAUSED | COMPLETED
    private Date startedAt;
    private Date pausedAt;
    private Date completedAt;
}
