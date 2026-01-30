package com.atlas_erp.iam_service.Repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;
import com.atlas_erp.iam_service.Entity.RoleEntity;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<RoleEntity, UUID> {

    Optional<RoleEntity> findByRoleCode(String roleCode);
}
