package com.atlas_erp.iam_service.Dto.Response.Permission;

import lombok.Data;
import java.util.List;

import com.atlas_erp.iam_service.Dto.DataScopeDto;

@Data
public class MyPermissionResponse {
    private List<String> roles;
    private List<String> permissions;
    private List<DataScopeDto> dataScopes;
}
