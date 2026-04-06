// File: src/main/java/org/atlas_erp/repository/WorkflowStepRepository.java
package org.atlas_erp.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.atlas_erp.entity.WorkflowStepEntity; // Giả định bạn có entity này
import java.util.List;
import java.util.UUID;

@ApplicationScoped
public class WorkflowStepRepository implements PanacheRepositoryBase<WorkflowStepEntity, UUID> {

    public List<WorkflowStepEntity> findByWorkflowIdOrderByStepOrderAsc(UUID workflowId) {
        // Query theo kiểu Panache rất gọn
        return list("workflowId = ?1 order by stepOrder asc", workflowId);
    }
}