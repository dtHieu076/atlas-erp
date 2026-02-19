package com.atlas_erp.organization_service.Util;

// import org.springframework.security.core.Authentication;
// import org.springframework.security.core.context.SecurityContextHolder;
import java.util.Optional;
import java.util.UUID;

public class SecurityUtils {

    // BẮT BUỘC phải có chữ public static để gọi được từ Service mà không cần khởi
    // tạo
    // public static Optional<UUID> getCurrentUserId() {
    // Authentication authentication =
    // SecurityContextHolder.getContext().getAuthentication();

    // if (authentication == null || !authentication.isAuthenticated()
    // || "anonymousUser".equals(authentication.getPrincipal())) {
    // return Optional.empty();
    // }

    // try {
    // // Giả sử Gateway đẩy userId vào phần Name của Authentication
    // return Optional.of(UUID.fromString(authentication.getName()));
    // } catch (Exception e) {
    // return Optional.empty();
    // }
    // }

    public static Optional<UUID> getCurrentUserId() {
        return Optional.of(UUID.randomUUID()); // DEV MODE
    }

}