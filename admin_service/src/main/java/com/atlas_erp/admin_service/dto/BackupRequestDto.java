package com.atlas_erp.admin_service.dto;

import jakarta.validation.constraints.NotBlank;

public record BackupRequestDto(
        @NotBlank(message = "Target namespace is required") String targetNamespace) {
}
