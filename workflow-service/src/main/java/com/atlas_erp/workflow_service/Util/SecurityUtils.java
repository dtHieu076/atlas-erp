package com.atlas_erp.workflow_service.Util;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {

    public static Optional<UUID> getCurrentUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()
                || "anonymousUser".equals(authentication.getPrincipal().toString())) {
            return Optional.empty();
        }

        try {
            // Lấy ID từ principal (thường là 'sub' trong JWT)
            String userIdStr = authentication.getName();
            return Optional.of(UUID.fromString(userIdStr));
        } catch (Exception e) {
            return Optional.empty();
        }
    }
}