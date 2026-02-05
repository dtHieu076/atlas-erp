package com.atlas_erp.iam_service.Event.dtoEvent;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.util.UUID;
import java.time.Instant;

@Data
@AllArgsConstructor
public class RoleCreatedEvent {
    private UUID roleId;
    private String roleCode;
    private String roleName;
    private Instant createdAt;
}
