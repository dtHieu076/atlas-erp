package com.atlas_erp.workflow_service.Repository;

import java.util.List;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.atlas_erp.workflow_service.Entity.WorkflowStepEntity;

public interface WorkflowStepRepository
                extends JpaRepository<WorkflowStepEntity, UUID> {
        List<WorkflowStepEntity> findByWorkflowIdOrderByStepOrderAsc(UUID workflowId);

        // Truy vấn các step có roleId nằm trong danh sách roles của user
        @Query("SELECT s FROM WorkflowStepEntity s WHERE s.roleId IN :roleIds")
        List<WorkflowStepEntity> findByRoleIdIn(List<UUID> roleIds);
}
