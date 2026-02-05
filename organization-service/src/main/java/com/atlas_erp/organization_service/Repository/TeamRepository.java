package com.atlas_erp.organization_service.Repository;

import com.atlas_erp.organization_service.Entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface TeamRepository extends JpaRepository<TeamEntity, UUID> {
    // Tìm danh sách Team theo Department ID có phân trang
    Page<TeamEntity> findByDepartmentId(UUID departmentId, Pageable pageable);

    boolean existsByCodeAndDepartmentId(String code, UUID departmentId);
}
