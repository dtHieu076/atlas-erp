package com.atlas_erp.organization_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.util.UUID;

@Data
@Builder
public class PlantCreatedEvent {
    private UUID plantId;
    private UUID organizationId;
    private String code;
    private String name;
    private UUID createdBy;
}
