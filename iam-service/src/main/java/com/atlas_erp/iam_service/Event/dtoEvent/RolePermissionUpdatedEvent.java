package com.atlas_erp.iam_service.Event.dtoEvent;

import lombok.Data;
import lombok.AllArgsConstructor;
import java.util.UUID;
import java.util.List;
import java.time.Instant;

@Data
@AllArgsConstructor
public class RolePermissionUpdatedEvent {
    private UUID roleId;
    private List<UUID> permissionIds;
    private Instant occurredAt;
}
