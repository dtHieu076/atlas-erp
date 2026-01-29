package com.atlas_erp.organization_service.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class OrganizationHistoryItemDTO {

    private String entityType;
    private String entityId;
    private String changeType;
    private String oldValue;
    private String newValue;
    private LocalDateTime changedAt;
    private String changedBy;
}
