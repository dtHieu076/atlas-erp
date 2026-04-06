// File: src/main/java/org/atlas_erp/repository/WorkflowRepository.java
package org.atlas_erp.repository;

import io.quarkus.hibernate.orm.panache.PanacheRepositoryBase;
import jakarta.enterprise.context.ApplicationScoped;
import org.atlas_erp.entity.WorkflowEntity;
import java.util.UUID;

@ApplicationScoped // Tương đương @Repository trong Spring
public class WorkflowRepository implements PanacheRepositoryBase<WorkflowEntity, UUID> {
    // Các hàm custom nếu cần
}