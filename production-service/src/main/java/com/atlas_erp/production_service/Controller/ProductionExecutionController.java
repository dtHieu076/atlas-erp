package com.atlas_erp.production_service.Controller;

import com.atlas_erp.production_service.Dto.Request.ProductionExecution.*;
import com.atlas_erp.production_service.Dto.Response.ProductionExecution.*;
import com.atlas_erp.production_service.Service.ProductionExecutionService;
import lombok.RequiredArgsConstructor;
// import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.UUID;

@RestController
@RequestMapping("/api/production/executions")
@RequiredArgsConstructor
public class ProductionExecutionController {

    private final ProductionExecutionService productionExecutionService;

    // 5️⃣ POST /executions/start
    @PostMapping("/start")
    public StartExecutionResponse startExecution(@RequestBody StartExecutionRequest request) {
        return productionExecutionService.startExecution(request);
    }

    // 6️⃣ POST /executions/finish
    @PostMapping("/finish")
    public FinishExecutionResponse finishExecution(@RequestBody FinishExecutionRequest request) {
        return productionExecutionService.finishExecution(request);
    }

    // 4️⃣ POST /executions/pause
    @PostMapping("/pause")
    public PauseExecutionResponse pauseExecution(@RequestBody PauseExecutionRequest request) {
        return productionExecutionService.pauseExecution(request);
    }

    // 5️⃣ POST /executions/resume
    @PostMapping("/resume")
    public ResumeExecutionResponse resumeExecution(@RequestBody ResumeExecutionRequest request) {
        return productionExecutionService.resumeExecution(request);
    }

    // 🔟 GET /executions/{id}
    @GetMapping("/{id}")
    public ExecutionDetailResponse getExecutionDetail(@PathVariable UUID id) {
        return productionExecutionService.getExecutionDetail(id);
    }
}
