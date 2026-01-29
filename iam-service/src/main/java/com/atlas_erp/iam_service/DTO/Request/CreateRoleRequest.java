package com.atlas_erp.iam_service.DTO.Request;

import lombok.Data;

@Data
public class CreateRoleRequest {
    private String code;
    private String name;
    private String description;
}
