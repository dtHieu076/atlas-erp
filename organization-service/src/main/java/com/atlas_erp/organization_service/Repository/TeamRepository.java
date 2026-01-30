package com.atlas_erp.organization_service.Repository;

import com.atlas_erp.organization_service.Entity.TeamEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TeamRepository extends JpaRepository<TeamEntity, UUID> {
}
