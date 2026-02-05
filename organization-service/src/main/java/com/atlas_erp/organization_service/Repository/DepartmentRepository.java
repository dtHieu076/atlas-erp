package com.atlas_erp.organization_service.Repository;

import com.atlas_erp.organization_service.Entity.DepartmentEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

public interface DepartmentRepository extends JpaRepository<DepartmentEntity, UUID> {
    // Tìm danh sách phòng ban theo Plant ID có phân trang
    Page<DepartmentEntity> findByPlantId(UUID plantId, Pageable pageable);

    boolean existsByCodeAndPlantId(String code, UUID plantId);
}
