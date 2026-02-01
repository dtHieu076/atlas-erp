package com.atlas_erp.workflow_service.Service.Impl;

import com.atlas_erp.workflow_service.Dto.Request.WorkflowInstance.*;
import com.atlas_erp.workflow_service.Dto.Response.WorkflowInstance.*;
import com.atlas_erp.workflow_service.Service.WorkflowInstanceService;
import java.util.*;
import org.springframework.stereotype.Service;;

@Service
public class WorkflowInstanceServiceImpl implements WorkflowInstanceService {

    @Override
    public CreateWorkflowInstanceResponse createWorkflowInstance(
            CreateWorkflowInstanceRequest request) {

        CreateWorkflowInstanceResponse response = new CreateWorkflowInstanceResponse();

        response.setInstanceId(UUID.randomUUID().toString());
        return response;
    }

    @Override
    public ApproveWorkflowInstanceResponse approveInstance(
            String instanceId,
            ApproveWorkflowInstanceRequest request) {

        ApproveWorkflowInstanceResponse response = new ApproveWorkflowInstanceResponse();
        response.setSuccess(true);
        return response;
    }

    @Override
    public RejectWorkflowInstanceResponse rejectInstance(
            String instanceId,
            RejectWorkflowInstanceRequest request) {

        RejectWorkflowInstanceResponse response = new RejectWorkflowInstanceResponse();
        response.setSuccess(true);
        return response;
    }

    @Override
    public WorkflowHistoryResponse getApprovalHistory(String instanceId) {

        WorkflowHistoryResponse response = new WorkflowHistoryResponse();
        response.setList(Collections.emptyList());
        return response;
    }
}
