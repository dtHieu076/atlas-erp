package com.atlas_erp.production_service.Service;

import com.atlas_erp.production_service.Dto.Request.ProductionExecution.*;
import com.atlas_erp.production_service.Dto.Response.ProductionExecution.*;
import java.util.UUID;

public interface ProductionExecutionService {

    StartExecutionResponse startExecution(StartExecutionRequest request);

    FinishExecutionResponse finishExecution(FinishExecutionRequest request);

    PauseExecutionResponse pauseExecution(PauseExecutionRequest request);

    ResumeExecutionResponse resumeExecution(ResumeExecutionRequest request);

    ExecutionDetailResponse getExecutionDetail(UUID executionId);
}
