package com.atlas_erp.iam_service.Dto.Request.Permission;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Request dùng để tìm kiếm danh sách permission
 * API: GET /permissions
 *
 * Lưu ý:
 * - Đây là IAM hạ tầng
 * - Không phân trang vì số lượng permission thường nhỏ
 * - Các field đều optional
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PermissionSearchRequest {

    /**
     * Tên module nghiệp vụ
     * Ví dụ:
     * - IAM
     * - HR
     * - INVENTORY
     * - ACCOUNTING
     *
     * Nếu null → lấy toàn bộ permission
     */
    private String module;
}
