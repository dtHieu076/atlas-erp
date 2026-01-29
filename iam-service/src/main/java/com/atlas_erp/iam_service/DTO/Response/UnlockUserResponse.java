package com.atlas_erp.iam_service.DTO.Response;

import lombok.Data;
import java.time.Instant;

@Data
public class UnlockUserResponse {
    private Boolean success;
    private String userId;
    private String status;
    private Instant unlockedAt;
    private String unlockedBy;
}
