package com.atlas_erp.iam_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PermissionDto {
    private String id;
    private String module;
    private String function;
    private String action;
}
