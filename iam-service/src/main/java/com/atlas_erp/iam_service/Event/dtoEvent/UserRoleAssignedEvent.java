package com.atlas_erp.iam_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;
import java.util.List;
import java.util.UUID;

@Data
@Builder
public class UserRoleAssignedEvent {
    private UUID userId;
    private List<String> roleCodes; // Trả về Code để các service khác dễ check quyền
    private UUID assignedBy;
}