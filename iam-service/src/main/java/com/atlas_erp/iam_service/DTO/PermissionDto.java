package com.atlas_erp.iam_service.Dto;

import lombok.Data;

@Data
public class PermissionDto {
    private String id;
    private String module;
    private String function;
    private String action;
}
