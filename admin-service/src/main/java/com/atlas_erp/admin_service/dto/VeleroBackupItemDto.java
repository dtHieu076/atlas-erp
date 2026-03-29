package com.atlas_erp.admin_service.dto;

import java.time.LocalDateTime;

public record VeleroBackupItemDto(
        String name,
        String phase,
        String startTimestamp,
        String completionTimestamp,
        String expiration) {
}
