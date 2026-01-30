package com.atlas_erp.iam_service.Dto.Request.Role;

import lombok.Data;

@Data
public class CreateRoleRequest {
    private String code;
    private String name;
    private String description;
}
