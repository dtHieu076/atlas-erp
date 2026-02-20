package com.atlas_erp.workflow_service.Service.Impl;

import org.springframework.stereotype.Service;
import com.atlas_erp.workflow_service.Service.WorkflowQueryService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.atlas_erp.workflow_service.Util.SecurityUtils;
import com.atlas_erp.workflow_service.Cache.serviceCache.WorkflowCacheService;
// import com.atlas_erp.workflow_service.Client.IamClient;
import com.atlas_erp.workflow_service.Dto.External.UserRolesResponse;
import com.atlas_erp.workflow_service.Dto.Request.WorkflowQuery.*;
import com.atlas_erp.workflow_service.Dto.Response.WorkflowQuery.*;
import com.atlas_erp.workflow_service.Dto.Response.WorkflowQuery.AssignedWorkflowTaskResponse.WorkflowTaskDto;
import com.atlas_erp.workflow_service.Dto.Response.WorkflowQuery.WorkflowInstanceListResponse.WorkflowInstanceSummary;
import com.atlas_erp.workflow_service.Entity.WorkflowEntity;
import com.atlas_erp.workflow_service.Entity.WorkflowInstanceEntity;
import com.atlas_erp.workflow_service.Entity.WorkflowStepEntity;
import com.atlas_erp.workflow_service.Repository.WorkflowInstanceRepository;
import com.atlas_erp.workflow_service.Repository.WorkflowRepository;
import com.atlas_erp.workflow_service.Repository.WorkflowStepRepository;
import java.util.UUID;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.Optional;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Slf4j
public class WorkflowQueryServiceImpl implements WorkflowQueryService {

    private final WorkflowInstanceRepository instanceRepository;
    private final WorkflowRepository workflowRepository;
    private final WorkflowStepRepository stepRepository;
    private final WorkflowCacheService cacheService;
    // private final IamClient iamClient;

    @Override
    public WorkflowInstanceListResponse getWorkflowInstances(WorkflowInstanceListRequest request) {
        log.info("Fetching workflow instances with status: {}", request.getStatus());

        // 1. Kiểm tra Cache
        String fromStr = request.getFromDate() != null ? String.valueOf(request.getFromDate().getTime()) : "any";
        String toStr = request.getToDate() != null ? String.valueOf(request.getToDate().getTime()) : "any";

        WorkflowInstanceListResponse cached = cacheService.getInstancesCache(request.getStatus(), fromStr, toStr);
        if (cached != null) {
            log.info("🚀 Instances List Cache Hit");
            return cached;
        }

        // 2. Truy vấn dữ liệu từ DB (Cần bổ sung method trong Repository)
        List<WorkflowInstanceEntity> entities = instanceRepository.findByFilters(
                request.getStatus(),
                request.getFromDate(),
                request.getToDate());

        // 3. Lấy Map thông tin Workflow để lấy BusinessType (tránh N+1 query)
        List<WorkflowEntity> allWorkflows = workflowRepository.findAll();
        Map<java.util.UUID, String> workflowTypeMap = allWorkflows.stream()
                .collect(Collectors.toMap(WorkflowEntity::getId, WorkflowEntity::getBusinessType));

        // 4. Map sang Response DTO
        List<WorkflowInstanceSummary> summaries = entities.stream().map(e -> {
            WorkflowInstanceSummary summary = new WorkflowInstanceSummary();
            summary.setInstanceId(e.getId().toString());
            summary.setWorkflowId(e.getWorkflowId().toString());
            summary.setBusinessId(e.getBusinessId().toString());
            summary.setStatus(e.getStatus());
            summary.setBusinessType(workflowTypeMap.getOrDefault(e.getWorkflowId(), "UNKNOWN"));

            // Sử dụng dữ liệu audit từ Entity
            summary.setStartedAt(e.getCreatedAt());

            // Nếu trạng thái là COMPLETED hoặc REJECTED thì mới lấy updatedAt làm
            // completedAt
            if ("COMPLETED".equals(e.getStatus()) || "REJECTED".equals(e.getStatus())) {
                summary.setCompletedAt(e.getUpdatedAt());
            }

            return summary;
        }).collect(Collectors.toList());

        WorkflowInstanceListResponse response = new WorkflowInstanceListResponse();
        response.setInstances(summaries);

        // 5. Lưu Cache
        cacheService.putInstancesCache(request.getStatus(), fromStr, toStr, response);

        return response;
    }

    @Override
    public AssignedWorkflowTaskResponse getAssignedTasksForCurrentUser() {
        // // 1. Lấy userId từ Token qua Utility
        // UUID userId = SecurityUtils.getCurrentUserId()
        // .orElseThrow(() -> new RuntimeException("Unauthorized: User ID not found in
        // token"));

        // // 2. Kiểm tra Cache (Sử dụng đúng tên hàm trong WorkflowCacheService)
        // AssignedWorkflowTaskResponse cached =
        // cacheService.getAssignedTasksCache(userId.toString());
        // if (cached != null) {
        // log.info("🚀 Assigned Tasks Cache Hit for user: {}", userId);
        // return cached;
        // }

        // // 3. Gọi IAM Service để lấy danh sách Roles của người dùng
        // UserRolesResponse userRoles = iamClient.getUserRoles(userId.toString());
        // if (userRoles == null || userRoles.getRoles() == null ||
        // userRoles.getRoles().isEmpty()) {
        // return new AssignedWorkflowTaskResponse(new ArrayList<>());
        // }

        // // Chuyển đổi danh sách roleId từ String sang UUID để so sánh
        // List<UUID> roleIds = userRoles.getRoles().stream()
        // .map(r -> UUID.fromString(r.getRoleId()))
        // .collect(Collectors.toList());

        // // 4. Lấy các Workflow Instances đang trong trạng thái chờ duyệt
        // List<WorkflowInstanceEntity> activeInstances =
        // instanceRepository.findByStatus("IN_PROGRESS");

        // // 5. Lọc và ánh xạ các task mà user có quyền phê duyệt
        // List<WorkflowTaskDto> tasks = new ArrayList<>();

        // for (WorkflowInstanceEntity instance : activeInstances) {
        // // Lấy toàn bộ cấu hình step của workflow này (nên được cache ở level cao hơn
        // để
        // // tối ưu)
        // List<WorkflowStepEntity> workflowSteps = stepRepository
        // .findByWorkflowIdOrderByStepOrderAsc(instance.getWorkflowId());

        // // Tìm cấu hình của bước hiện tại (currentStep)
        // Optional<WorkflowStepEntity> currentStepOpt = workflowSteps.stream()
        // .filter(s -> s.getStepOrder().equals(instance.getCurrentStep()))
        // .findFirst();

        // if (currentStepOpt.isPresent()) {
        // WorkflowStepEntity stepConfig = currentStepOpt.get(); // Khai báo rõ ràng để
        // hết lỗi

        // // Kiểm tra xem user có Role phù hợp với bước này không
        // if (roleIds.contains(stepConfig.getRoleId())) {
        // WorkflowTaskDto dto = new WorkflowTaskDto();
        // dto.setTaskId(stepConfig.getId().toString());
        // dto.setInstanceId(instance.getId().toString());
        // dto.setStepName("Phê duyệt bước " + instance.getCurrentStep());

        // // Xác định thời điểm task được gán (mốc tính SLA)
        // Date assignedAt = (instance.getUpdatedAt() != null) ? instance.getUpdatedAt()
        // : new Date();
        // dto.setAssignedAt(assignedAt);

        // // TÍNH TOÁN DUE DATE DỰA TRÊN SLA (durationHours)
        // Calendar cal = Calendar.getInstance();
        // cal.setTime(assignedAt);

        // int hoursToAdd = (stepConfig.getDurationHours() != null) ?
        // stepConfig.getDurationHours() : 24;
        // cal.add(Calendar.HOUR, hoursToAdd);

        // dto.setDueDate(cal.getTime());

        // tasks.add(dto);
        // }
        // }
        // }

        // // 6. Tạo response và lưu vào Cache trước khi trả về
        // AssignedWorkflowTaskResponse response = new AssignedWorkflowTaskResponse();
        // response.setTasks(tasks);

        // cacheService.putAssignedTasksCache(userId.toString(), response);

        // return response;
        return null; // Tạm thời trả về null vì phần lấy task đã bị comment để tập trung vào phần lấy
                     // danh sách workflow
    }
}
