package com.atlas_erp.workflow_service.Service.Impl;

import org.springframework.stereotype.Service;
import com.atlas_erp.workflow_service.Service.WorkflowService;
import com.atlas_erp.workflow_service.Dto.Request.Workflow.*;
import com.atlas_erp.workflow_service.Dto.Response.Workflow.*;
import java.util.*;

@Service
public class WorkflowServiceImpl implements WorkflowService {

    @Override
    public CreateWorkflowResponse createWorkflow(CreateWorkflowRequest request) {

        CreateWorkflowResponse response = new CreateWorkflowResponse();
        response.setId(UUID.randomUUID().toString());
        return response;
    }

    @Override
    public WorkflowListResponse getWorkflows(WorkflowListRequest request) {

        WorkflowListResponse response = new WorkflowListResponse();
        response.setWorkflows(Collections.emptyList());
        return response;
    }
}
