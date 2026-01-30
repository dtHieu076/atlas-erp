package com.atlas_erp.iam_service.Dto.Request.User;

import lombok.Data;

@Data
public class CreateUserRequest {
    private String username;
    private String password;
    private String employeeId;
    private String status;
}
