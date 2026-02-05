package com.atlas_erp.organization_service.Event.dtoEvent;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TeamCreatedEvent {
    private UUID teamId;
    private UUID departmentId;
    private UUID leaderId;
    private UUID createdBy;
    private String code;
    private String name;
}