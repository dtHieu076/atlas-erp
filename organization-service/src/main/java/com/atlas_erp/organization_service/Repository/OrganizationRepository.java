package com.atlas_erp.organization_service.Repository;

import com.atlas_erp.organization_service.Entity.OrganizationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface OrganizationRepository extends JpaRepository<OrganizationEntity, UUID> {
}
