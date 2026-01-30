package com.atlas_erp.iam_service.Dto.Request.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request buộc người dùng đổi mật khẩu
 * API: POST /users/{id}/force-password-reset
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ForcePasswordResetRequest {

    /**
     * Lý do buộc reset mật khẩu
     * (ghi audit log / security event)
     */
    private String reason;
}
