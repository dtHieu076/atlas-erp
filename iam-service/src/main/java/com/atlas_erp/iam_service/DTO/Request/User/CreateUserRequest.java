package com.atlas_erp.iam_service.Dto.Request.User;

import lombok.Data;

@Data
public class CreateUserRequest {

    /**
     * Google unique user id (sub)
     * BẮT BUỘC
     */
    private String googleUid;

    /**
     * Email từ Google
     * Dùng làm username hiển thị
     */
    private String email;

    /**
     * Liên kết sang HR (optional)
     */
    private String employeeId;

    /**
     * ACTIVE | INACTIVE | LOCKED
     */
    private String status;
}
