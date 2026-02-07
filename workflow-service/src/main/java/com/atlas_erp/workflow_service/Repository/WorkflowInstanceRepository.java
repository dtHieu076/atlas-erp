package com.atlas_erp.workflow_service.Repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Date;
import java.util.List;
import com.atlas_erp.workflow_service.Entity.WorkflowInstanceEntity;

public interface WorkflowInstanceRepository
                extends JpaRepository<WorkflowInstanceEntity, UUID> {
        @Query("SELECT w FROM WorkflowInstanceEntity w WHERE " +
                        "(:status IS NULL OR w.status = :status) AND " +
                        "(:fromDate IS NULL OR w.createdAt >= :fromDate) AND " +
                        "(:toDate IS NULL OR w.createdAt <= :toDate)")
        List<WorkflowInstanceEntity> findByFilters(
                        @Param("status") String status,
                        @Param("fromDate") Date fromDate,
                        @Param("toDate") Date toDate);// Thêm dòng này để Spring JPA tự động tạo câu query lọc theo
                                                      // Status

        List<WorkflowInstanceEntity> findByStatus(String status);
}
