package com.atlas_erp.workflow_service.Service.Impl;

import org.springframework.stereotype.Service;
import com.atlas_erp.workflow_service.Service.WorkflowService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import com.atlas_erp.workflow_service.Cache.serviceCache.WorkflowCacheService;
import com.atlas_erp.workflow_service.Client.IamClient;
import com.atlas_erp.workflow_service.Dto.External.RoleListResponse;
import com.atlas_erp.workflow_service.Dto.Request.Workflow.*;
import com.atlas_erp.workflow_service.Dto.Response.Workflow.*;
import com.atlas_erp.workflow_service.Dto.Response.Workflow.WorkflowListResponse.WorkflowSummary;
import com.atlas_erp.workflow_service.Entity.WorkflowEntity;
import com.atlas_erp.workflow_service.Entity.WorkflowStepEntity;
import com.atlas_erp.workflow_service.Repository.WorkflowRepository;
import com.atlas_erp.workflow_service.Repository.WorkflowStepRepository;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowServiceImpl implements WorkflowService {

    private final WorkflowRepository workflowRepository;
    private final WorkflowStepRepository stepRepository;
    private final WorkflowCacheService cacheService;
    private final IamClient iamClient;

    @Override
    @Transactional
    public CreateWorkflowResponse createWorkflow(CreateWorkflowRequest request) {
        log.info("Creating new workflow: {} - {}", request.getCode(), request.getName());

        // 1. Validate: Kiểm tra code duy nhất
        if (workflowRepository.existsByCode(request.getCode())) {
            throw new RuntimeException("Workflow code already exists: " + request.getCode());
        }

        // 2. IAM Service: Lấy danh sách Role để resolve Code -> ID
        RoleListResponse roleListResponse = iamClient.getAllRoles();
        Map<String, String> roleMap = roleListResponse.getRoles().stream()
                .collect(Collectors.toMap(RoleListResponse.RoleDto::getCode, RoleListResponse.RoleDto::getId));

        // 3. Tạo Workflow Entity
        WorkflowEntity workflow = new WorkflowEntity();
        workflow.setCode(request.getCode());
        workflow.setName(request.getName());
        workflow.setBusinessType(request.getBusinessType());
        workflow.setDescription(request.getDescription());
        workflow.setStatus("ACTIVE");
        workflow.setVersion(1);

        WorkflowEntity savedWorkflow = workflowRepository.save(workflow);

        // 4. Tạo Workflow Step Entities
        List<WorkflowStepEntity> stepEntities = request.getSteps().stream().map(stepReq -> {
            // Validate role tồn tại
            String roleId = roleMap.get(stepReq.getRoleCode());
            if (roleId == null) {
                throw new RuntimeException("Role code not found in IAM: " + stepReq.getRoleCode());
            }

            WorkflowStepEntity step = new WorkflowStepEntity();
            step.setWorkflowId(savedWorkflow.getId());
            step.setStepOrder(stepReq.getStepOrder());
            step.setRoleId(UUID.fromString(roleId));
            step.setActionType(stepReq.getActionType());
            // Bạn có thể set mặc định durationHours ở đây nếu cần
            step.setDurationHours(24);

            return step;
        }).collect(Collectors.toList());

        stepRepository.saveAll(stepEntities);

        log.info("Successfully created workflow with ID: {}", savedWorkflow.getId());

        CreateWorkflowResponse response = new CreateWorkflowResponse();
        response.setId(savedWorkflow.getId().toString());
        return response;
    }

    @Override
    public WorkflowListResponse getWorkflows(WorkflowListRequest request) {
        log.info("Fetching workflow list with type: {}, status: {}", request.getType(), request.getStatus());

        // 1. Thử lấy từ Cache
        WorkflowListResponse cachedResponse = cacheService.getWorkflowsCache(request.getType(), request.getStatus());
        if (cachedResponse != null) {
            log.info("🚀 Workflow List Cache Hit");
            return cachedResponse;
        }

        // 2. Truy vấn Database với filter động
        List<WorkflowEntity> entities = workflowRepository.findByFilters(
                request.getType(),
                request.getStatus());

        // 3. Map Entity sang DTO
        List<WorkflowSummary> summaries = entities.stream().map(entity -> {
            WorkflowSummary summary = new WorkflowSummary();
            summary.setWorkflowId(entity.getId().toString());
            summary.setWorkflowCode(entity.getCode());
            summary.setWorkflowName(entity.getName());
            summary.setType(entity.getBusinessType());
            summary.setStatus(entity.getStatus());
            summary.setVersion(entity.getVersion());
            return summary;
        }).collect(Collectors.toList());

        WorkflowListResponse response = new WorkflowListResponse();
        response.setWorkflows(summaries);

        // 4. Lưu vào Cache cho lần gọi sau
        cacheService.putWorkflowsCache(request.getType(), request.getStatus(), response);

        return response;
    }
}
