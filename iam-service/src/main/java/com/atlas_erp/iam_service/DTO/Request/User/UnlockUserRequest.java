package com.atlas_erp.iam_service.Dto.Request.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request mở khóa tài khoản người dùng
 * API: POST /users/{id}/unlock
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnlockUserRequest {

    /**
     * Lý do mở khóa tài khoản
     * (ghi audit log)
     */
    private String reason;
}
