package com.atlas_erp.iam_service.DTO.Response;

import lombok.Data;
import java.time.Instant;

@Data
public class UserDetailResponse {
    private String userId;
    private String username;
    private String fullName;
    private String email;
    private String phone;
    private String status;

    private Instant createdAt;
    private Instant lastLoginAt;
}
