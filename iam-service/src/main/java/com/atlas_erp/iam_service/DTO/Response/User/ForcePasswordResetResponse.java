package com.atlas_erp.iam_service.Dto.Response.User;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ForcePasswordResetResponse {

    private Boolean success;
    private String userId;
    private Boolean forceReset;
    private Instant updatedAt;
}
