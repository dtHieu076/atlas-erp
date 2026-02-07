package com.atlas_erp.workflow_service.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.List;
import org.springframework.data.repository.query.Param;
import com.atlas_erp.workflow_service.Entity.WorkflowEntity;

public interface WorkflowRepository
                extends JpaRepository<WorkflowEntity, UUID> {
        boolean existsByCode(String code);

        @Query("SELECT w FROM WorkflowEntity w WHERE " +
                        "(:type IS NULL OR w.businessType = :type) AND " +
                        "(:status IS NULL OR w.status = :status)")
        List<WorkflowEntity> findByFilters(
                        @Param("type") String type,
                        @Param("status") String status);
}
