package com.atlas_erp.admin_service.repository;

import com.atlas_erp.admin_service.entity.BackupJob;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface BackupJobRepository extends JpaRepository<BackupJob, UUID> {
}
