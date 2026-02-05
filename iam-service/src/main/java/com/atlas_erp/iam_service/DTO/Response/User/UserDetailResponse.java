package com.atlas_erp.iam_service.Dto.Response.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDetailResponse {
    private String userId;
    private UUID employeeId; // QUAN TRỌNG: Để Gateway dùng gọi sang HR Service
    private String username;
    private String email;
    private String status;
    private Instant createdAt;
    private Instant lastLoginAt;
}