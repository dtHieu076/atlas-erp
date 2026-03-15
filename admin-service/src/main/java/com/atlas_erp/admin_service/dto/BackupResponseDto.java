package com.atlas_erp.admin_service.dto;

import com.atlas_erp.admin_service.enums.BackupStatus;
import java.time.LocalDateTime;
import java.util.UUID;

public record BackupResponseDto(
        UUID id,
        String veleroBackupName,
        String targetNamespace,
        BackupStatus status,
        LocalDateTime createdAt) {
}
