package com.atlas_erp.iam_service.DTO.Response;

import lombok.Data;
import java.time.Instant;

@Data
public class ForcePasswordResetResponse {
    private Boolean success;
    private String userId;
    private Boolean forceReset;
    private Instant updatedAt;
}
