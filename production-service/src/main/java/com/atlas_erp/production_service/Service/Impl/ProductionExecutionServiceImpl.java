package com.atlas_erp.production_service.Service.Impl;

import com.atlas_erp.production_service.Dto.Request.ProductionExecution.*;
import com.atlas_erp.production_service.Dto.Response.ProductionExecution.*;
// import com.atlas_erp.production_service.Entity.ProductionExecutionEntity;
// import com.atlas_erp.production_service.Repository.ProductionExecutionRepository;
import com.atlas_erp.production_service.Service.ProductionExecutionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.UUID;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductionExecutionServiceImpl implements ProductionExecutionService {

    // private final ProductionExecutionRepository repository;

    @Override
    public StartExecutionResponse startExecution(StartExecutionRequest request) {

        StartExecutionResponse response = new StartExecutionResponse();

        // sinh executionId giả để FE / integration test dùng
        response.setExecutionId(UUID.randomUUID().toString());

        return response;
    }

    @Override
    public FinishExecutionResponse finishExecution(FinishExecutionRequest request) {
        return FinishExecutionResponse.success();
    }

    @Override
    public PauseExecutionResponse pauseExecution(PauseExecutionRequest request) {

        PauseExecutionResponse response = new PauseExecutionResponse();

        response.setSuccess(true);
        response.setExecutionId(request.getExecutionId());
        response.setStatus("PAUSED");
        response.setPausedAt(new Date());

        return response;
    }

    @Override
    public ResumeExecutionResponse resumeExecution(ResumeExecutionRequest request) {

        ResumeExecutionResponse response = new ResumeExecutionResponse();

        response.setSuccess(true);
        response.setExecutionId(request.getExecutionId());
        response.setStatus("RUNNING");
        response.setResumedAt(new Date());

        return response;
    }

    @Override
    public ExecutionDetailResponse getExecutionDetail(UUID executionId) {

        ExecutionDetailResponse response = new ExecutionDetailResponse();

        response.setExecutionId(executionId.toString());

        // productionPlanId giả
        response.setProductionPlanId(UUID.randomUUID().toString());

        // trạng thái giả (mặc định RUNNING)
        response.setStatus("RUNNING");

        Date now = new Date();

        // thời gian giả
        response.setStartedAt(new Date(now.getTime() - 60 * 60 * 1000)); // chạy từ 1 tiếng trước
        response.setPausedAt(null);
        response.setCompletedAt(null);

        return response;
    }

}
