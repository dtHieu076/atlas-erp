package com.atlas_erp.iam_service.Dto.Response.User;

import lombok.Data;
import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnlockUserResponse {

    private Boolean success;
    private String userId;
    private String status;
    private Instant unlockedAt;
    private String unlockedBy;
}
