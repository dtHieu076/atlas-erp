package com.atlas_erp.iam_service.Dto;

import lombok.Data;

@Data
public class UserDataScopeDto {
    private String scopeType;
    private String scopeId;
    private String scopeName;
}
