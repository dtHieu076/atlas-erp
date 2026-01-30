package com.atlas_erp.iam_service.Dto;

import lombok.Data;

@Data
public class DataScopeDto {
    private String type; // ORGANIZATION / DEPARTMENT / WAREHOUSE / TEAM
    private String scopeId;
}
