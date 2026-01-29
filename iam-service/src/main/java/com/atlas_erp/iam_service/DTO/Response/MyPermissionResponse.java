package com.atlas_erp.iam_service.DTO.Response;

import com.atlas_erp.iam_service.DTO.DataScopeDto;
import lombok.Data;
import java.util.List;

@Data
public class MyPermissionResponse {
    private List<String> roles;
    private List<String> permissions;
    private List<DataScopeDto> dataScopes;
}
