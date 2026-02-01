package com.atlas_erp.workflow_service.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import com.atlas_erp.workflow_service.Entity.WorkflowInstanceEntity;

public interface WorkflowInstanceRepository
        extends JpaRepository<WorkflowInstanceEntity, UUID> {
}
