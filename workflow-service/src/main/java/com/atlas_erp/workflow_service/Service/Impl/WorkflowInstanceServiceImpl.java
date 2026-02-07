package com.atlas_erp.workflow_service.Service.Impl;

import com.atlas_erp.workflow_service.Cache.serviceCache.WorkflowCacheService;
import com.atlas_erp.workflow_service.Dto.Request.WorkflowInstance.*;
import com.atlas_erp.workflow_service.Dto.Response.WorkflowInstance.*;
import com.atlas_erp.workflow_service.Entity.WorkflowEntity;
import com.atlas_erp.workflow_service.Entity.WorkflowInstanceEntity;
import com.atlas_erp.workflow_service.Entity.WorkflowStepEntity;
import com.atlas_erp.workflow_service.Event.serviceEvent.WorkflowEventService;
import com.atlas_erp.workflow_service.Repository.WorkflowInstanceRepository;
import com.atlas_erp.workflow_service.Repository.WorkflowRepository;
import com.atlas_erp.workflow_service.Repository.WorkflowStepRepository;
import com.atlas_erp.workflow_service.Service.WorkflowInstanceService;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import org.springframework.stereotype.Service;;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowInstanceServiceImpl implements WorkflowInstanceService {

    private final WorkflowInstanceRepository instanceRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowStepRepository stepRepository;
    private final WorkflowCacheService cacheService;
    private final WorkflowEventService eventService;

    @Override
    @Transactional
    public CreateWorkflowInstanceResponse createWorkflowInstance(CreateWorkflowInstanceRequest request) {
        log.info("Creating workflow instance for BusinessID: {}", request.getBusinessId());

        UUID workflowUuid = UUID.fromString(request.getWorkflowId());
        UUID businessUuid = UUID.fromString(request.getBusinessId());

        // 1. Lấy thông tin Workflow Definition (Ưu tiên Cache)
        WorkflowEntity workflow = cacheService.getWorkflow(workflowUuid);
        if (workflow == null) {
            workflow = workflowRepository.findById(workflowUuid)
                    .orElseThrow(() -> new RuntimeException("Workflow definition not found"));
            cacheService.putWorkflow(workflow);
        }

        // 2. Khởi tạo Instance thực thi
        WorkflowInstanceEntity instance = new WorkflowInstanceEntity();
        instance.setWorkflowId(workflowUuid);
        instance.setBusinessId(businessUuid);

        // Luồng duyệt luôn bắt đầu từ bước đầu tiên
        instance.setCurrentStep(1);
        instance.setStatus("IN_PROGRESS");

        // 3. Lưu vào Database
        WorkflowInstanceEntity savedInstance = instanceRepository.save(instance);

        // 4. Trả về kết quả
        CreateWorkflowInstanceResponse response = new CreateWorkflowInstanceResponse();
        response.setInstanceId(savedInstance.getId().toString());

        log.info("✅ Workflow instance started: {}", response.getInstanceId());
        return response;
    }

    @Override
    @Transactional
    public ApproveWorkflowInstanceResponse approveInstance(
            String instanceId,
            ApproveWorkflowInstanceRequest request) {

        log.info("Approving instance: {} by user: {}", instanceId, request.getApproverId());

        // 1. Tìm Instance hiện tại
        WorkflowInstanceEntity instance = instanceRepository.findById(UUID.fromString(instanceId))
                .orElseThrow(() -> new RuntimeException("Workflow instance not found"));

        if (!"IN_PROGRESS".equals(instance.getStatus())) {
            throw new RuntimeException("Workflow is not in a state that can be approved");
        }

        // 2. Lấy danh sách các bước của Workflow này để kiểm tra bước tiếp theo
        List<WorkflowStepEntity> steps = stepRepository.findByWorkflowIdOrderByStepOrderAsc(instance.getWorkflowId());

        Integer currentStepOrder = instance.getCurrentStep();
        boolean hasNextStep = steps.stream().anyMatch(s -> s.getStepOrder() > currentStepOrder);

        if (hasNextStep) {
            // ✅ TRƯỜNG HỢP 1: CHUYỂN BƯỚC
            instance.setCurrentStep(currentStepOrder + 1);
            instanceRepository.save(instance);

            // Phát event chuyển bước (để gửi thông báo cho người duyệt tiếp theo)
            eventService.publishStepApproved(instance.getId(), currentStepOrder, "GENERAL");
            log.info("Step {} approved, moved to step {}", currentStepOrder, instance.getCurrentStep());
        } else {
            // ✅ TRƯỜNG HỢP 2: HOÀN TẤT WORKFLOW
            instance.setStatus("COMPLETED");
            instance.setCurrentStep(currentStepOrder); // Giữ nguyên bước cuối
            instanceRepository.save(instance);

            // Phát event hoàn tất (Để các service nghiệp vụ update status = APPROVED)
            // Cần lấy businessType từ WorkflowEntity nếu muốn chi tiết hơn
            eventService.publishInstanceCompleted(instance.getId(), instance.getBusinessId(), "BUSINESS_DOC");
            log.info("Workflow completed for instance: {}", instanceId);
        }

        ApproveWorkflowInstanceResponse response = new ApproveWorkflowInstanceResponse();
        response.setSuccess(true);
        return response;
    }

    @Override
    @Transactional
    public RejectWorkflowInstanceResponse rejectInstance(
            String instanceId,
            RejectWorkflowInstanceRequest request) {

        log.warn("🚨 Rejecting instance: {} by user: {}. Reason: {}",
                instanceId, request.getApproverId(), request.getReason());

        // 1. Tìm Instance hiện tại
        WorkflowInstanceEntity instance = instanceRepository.findById(UUID.fromString(instanceId))
                .orElseThrow(() -> new RuntimeException("Workflow instance not found"));

        // Chỉ được reject khi đang trong quá trình duyệt
        if (!"IN_PROGRESS".equals(instance.getStatus())) {
            throw new RuntimeException("Workflow is not in a state that can be rejected");
        }

        // 2. Cập nhật trạng thái Instance thành REJECTED
        // Lưu ý: Ta giữ nguyên currentStep để biết luồng bị dừng ở bước nào
        instance.setStatus("REJECTED");

        // Lưu xuống DB
        instanceRepository.save(instance);

        // 3. Phát event REJECTED qua Kafka
        // Giả sử businessType có thể lấy từ WorkflowEntity tương ứng
        eventService.publishInstanceRejected(
                instance.getId(),
                instance.getBusinessId(),
                "BUSINESS_DOC",
                request.getReason());

        log.info("✅ Instance {} has been officially REJECTED", instanceId);

        RejectWorkflowInstanceResponse response = new RejectWorkflowInstanceResponse();
        response.setSuccess(true);
        return response;
    }

    @Override // Chưa cần thiết
    public WorkflowHistoryResponse getApprovalHistory(String instanceId) {

        WorkflowHistoryResponse response = new WorkflowHistoryResponse();
        response.setList(Collections.emptyList());
        return response;
    }
}
