package com.atlas_erp.iam_service.Dto.Request.User;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request tìm kiếm danh sách user
 * API: GET /users
 *
 * Tất cả field đều optional trừ page, pageSize
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserSearchRequest {

    /**
     * Username (search gần đúng)
     */
    private String username;

    /**
     * Trạng thái user
     * Ví dụ: ACTIVE, LOCKED, DISABLED
     */
    private String status;

    /**
     * Role code (ví dụ: ADMIN, HR, ACCOUNTANT)
     */
    private String role;

    /**
     * Trang hiện tại (0-based)
     */
    private Integer page;

    /**
     * Số bản ghi mỗi trang
     */
    private Integer pageSize;
}
