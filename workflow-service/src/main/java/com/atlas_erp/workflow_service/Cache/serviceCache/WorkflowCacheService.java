package com.atlas_erp.workflow_service.Cache.serviceCache;

import com.atlas_erp.workflow_service.Dto.Response.Workflow.WorkflowListResponse;
import com.atlas_erp.workflow_service.Dto.Response.WorkflowQuery.AssignedWorkflowTaskResponse;
import com.atlas_erp.workflow_service.Dto.Response.WorkflowQuery.WorkflowInstanceListResponse;
import com.atlas_erp.workflow_service.Entity.WorkflowEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WorkflowCacheService {

    private static final Duration TTL = Duration.ofHours(2); // Cấu hình workflow ít thay đổi nên để TTL dài
    private final RedisTemplate<String, Object> redisTemplate;

    private String workflowKey(UUID workflowId) {
        return "workflow:definition:" + workflowId;
    }

    public WorkflowEntity getWorkflow(UUID workflowId) {
        return (WorkflowEntity) redisTemplate.opsForValue().get(workflowKey(workflowId));
    }

    public void putWorkflow(WorkflowEntity workflow) {
        redisTemplate.opsForValue().set(workflowKey(workflow.getId()), workflow, TTL);
    }

    public void evictWorkflow(UUID workflowId) {
        redisTemplate.delete(workflowKey(workflowId));
    }// Bổ sung vào WorkflowCacheService.java

    public WorkflowInstanceListResponse getInstancesCache(String status, String from, String to) {
        // Tạo key duy nhất dựa trên các tham số filter
        String key = String.format("workflow:instances:list:%s:%s:%s", status, from, to);
        return (WorkflowInstanceListResponse) redisTemplate.opsForValue().get(key);
    }

    public void putInstancesCache(String status, String from, String to, WorkflowInstanceListResponse response) {
        String key = String.format("workflow:instances:list:%s:%s:%s", status, from, to);
        // Cache danh sách thường để TTL ngắn hơn (ví dụ 5 phút) vì dữ liệu thay đổi
        // liên tục
        redisTemplate.opsForValue().set(key, response, Duration.ofMinutes(5));
    }// Bổ sung vào file WorkflowCacheService.java

    public AssignedWorkflowTaskResponse getAssignedTasksCache(String userId) {
        String key = "workflow:tasks:assigned:" + userId;
        return (AssignedWorkflowTaskResponse) redisTemplate.opsForValue().get(key);
    }

    public void putAssignedTasksCache(String userId, AssignedWorkflowTaskResponse response) {
        String key = "workflow:tasks:assigned:" + userId;
        // Inbox cá nhân thay đổi thường xuyên, nên để TTL ngắn (ví dụ: 2-5 phút)
        redisTemplate.opsForValue().set(key, response, Duration.ofMinutes(2));
    }

    public void evictAssignedTasksCache(String userId) {
        redisTemplate.delete("workflow:tasks:assigned:" + userId);
    }// Bổ sung vào WorkflowCacheService.java

    public WorkflowListResponse getWorkflowsCache(String type, String status) {
        String key = String.format("workflow:list:%s:%s",
                (type == null ? "all" : type),
                (status == null ? "all" : status));
        return (WorkflowListResponse) redisTemplate.opsForValue().get(key);
    }

    public void putWorkflowsCache(String type, String status, WorkflowListResponse response) {
        String key = String.format("workflow:list:%s:%s",
                (type == null ? "all" : type),
                (status == null ? "all" : status));
        // Dữ liệu cấu hình workflow ít thay đổi, có thể để TTL 1 giờ
        redisTemplate.opsForValue().set(key, response, Duration.ofHours(1));
    }
}