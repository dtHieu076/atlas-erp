package com.atlas_erp.iam_service.util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {

    public static Optional<UUID> getCurrentUserId() {
        // Lấy thông tin từ SecurityContext của Spring
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Kiểm tra xem đã đăng nhập chưa
        if (auth == null || !auth.isAuthenticated()) {
            return Optional.empty();
        }

        try {
            // auth.getName() chứa userId (Subject) từ JWT
            return Optional.of(UUID.fromString(auth.getName()));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}