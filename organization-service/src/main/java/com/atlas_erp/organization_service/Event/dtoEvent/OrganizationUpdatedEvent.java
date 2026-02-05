package com.atlas_erp.organization_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class OrganizationUpdatedEvent {
    private UUID organizationId;
    private String name;
    private String status;
    private UUID updatedBy;
}
