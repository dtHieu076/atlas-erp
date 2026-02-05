package com.atlas_erp.iam_service.Event.dtoEvent;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.UUID;

@Data
@Builder
public class UserStatusUpdatedEvent {

    private UUID userId;
    private String status;
    private Instant updatedAt;
}
