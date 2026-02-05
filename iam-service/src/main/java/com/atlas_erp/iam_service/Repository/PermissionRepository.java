package com.atlas_erp.iam_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import com.atlas_erp.iam_service.Entity.PermissionEntity;
import java.util.List;

@Repository
public interface PermissionRepository extends JpaRepository<PermissionEntity, UUID> {

    List<PermissionEntity> findByModule(String module);

    List<PermissionEntity> findAllById(Iterable<UUID> ids);
}
