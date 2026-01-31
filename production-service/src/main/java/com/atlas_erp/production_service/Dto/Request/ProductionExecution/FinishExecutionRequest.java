package com.atlas_erp.production_service.Dto.Request.ProductionExecution;

import lombok.Data;
import java.util.Date;

@Data
public class FinishExecutionRequest {

    private String executionId;
    private Integer goodQty;
    private Integer defectQty;
    private Date finishedAt;
    private String note;
}
