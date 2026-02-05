package com.atlas_erp.iam_service.Repository;

import com.atlas_erp.iam_service.Entity.RolePermissionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RolePermissionRepository
        extends JpaRepository<RolePermissionEntity, UUID> {

    void deleteByRoleId(UUID roleId);

    List<RolePermissionEntity> findByRoleId(UUID roleId);

    boolean existsByRoleIdAndPermissionId(UUID roleId, UUID permissionId);
}
