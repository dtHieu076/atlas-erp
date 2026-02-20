package com.atlas_erp.hr_service.Repository;

import com.atlas_erp.hr_service.Entity.JobTitleEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobTitleRepository extends
        JpaRepository<JobTitleEntity, UUID>,
        JpaSpecificationExecutor<JobTitleEntity> {

    // ========================
    // Basic Queries
    // ========================

    Optional<JobTitleEntity> findByCode(String code);

    boolean existsByCode(String code);

    List<JobTitleEntity> findByDepartmentId(UUID departmentId);

    List<JobTitleEntity> findByStatus(String status);

    List<JobTitleEntity> findByDepartmentIdAndStatus(UUID departmentId, String status);

}