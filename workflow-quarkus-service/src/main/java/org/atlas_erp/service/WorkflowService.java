// File: src/main/java/org/atlas_erp/service/WorkflowService.java
package org.atlas_erp.service;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.atlas_erp.dto.WorkflowAllListResponse;
import org.atlas_erp.repository.WorkflowRepository;
import org.atlas_erp.repository.WorkflowStepRepository;
import org.atlas_erp.entity.WorkflowEntity;

import java.util.List;

@ApplicationScoped // Tương đương @Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowStepRepository stepRepository;

    public List<WorkflowAllListResponse> getAllWorkflows() {
        // Thay findAll() của Spring bằng listAll() của Panache
        List<WorkflowEntity> workflows = workflowRepository.listAll();

        return workflows.stream().map(wf -> {
            List<WorkflowAllListResponse.WorkflowStepResponse> steps = stepRepository
                    .findByWorkflowIdOrderByStepOrderAsc(wf.getId())
                    .stream()
                    .map(step -> WorkflowAllListResponse.WorkflowStepResponse.builder()
                            .id(step.getId())
                            .workflowId(step.getWorkflowId())
                            .name(step.getName())
                            // ... map các field khác
                            .build())
                    .toList();

            return WorkflowAllListResponse.builder()
                    .id(wf.getId())
                    .code(wf.getCode())
                    .name(wf.getName())
                    .businessType(wf.getBusinessType())
                    .description(wf.getDescription())
                    .status(wf.getStatus())
                    .version(wf.getVersion())
                    .steps(steps)
                    .build();
        }).toList();
    }
}