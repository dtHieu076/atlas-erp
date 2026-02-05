package com.atlas_erp.organization_service.Event.dtoEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrganizationCreatedEvent {
    private UUID organizationId;
    private String code;
    private String name;
    private UUID createdBy; // UserId để IAM gán Data Scope
    private Instant createdAt;
}
