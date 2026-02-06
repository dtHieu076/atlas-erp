package com.atlas_erp.inventory_service.Util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import java.util.UUID;

/**
 * Tiện ích hỗ trợ lấy thông tin người dùng từ Security Context (Token)
 */
public class SecurityUtils {

    /**
     * Lấy UUID của người dùng đang đăng nhập
     * 
     * @return Optional chứa UUID hoặc Empty nếu chưa xác thực
     */
    public static Optional<UUID> getCurrentUserId() {
        // Lấy thông tin xác thực từ context của Spring Security
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra xem user đã login chưa
        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            return Optional.empty();
        }

        try {
            /*
             * Lưu ý: Tùy vào cách bạn cấu hình JwtAuthenticationConverter.
             * Thông thường, authentication.getName() sẽ trả về "sub" (UserID)
             * được trích xuất từ JWT token.
             */
            String userIdStr = authentication.getName();
            return Optional.of(UUID.fromString(userIdStr));
        } catch (Exception e) {
            // Log lỗi nếu format ID không phải là UUID hợp lệ
            return Optional.empty();
        }
    }
}