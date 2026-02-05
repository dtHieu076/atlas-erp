package com.atlas_erp.iam_service.Event.dtoEvent;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserLoggedOutEvent {

    private UUID userId;

    private Instant loggedOutAt;

    private String reason;
}
