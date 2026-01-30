package com.atlas_erp.iam_service.Dto.Response.Permission;

import lombok.Data;
import java.util.List;

import com.atlas_erp.iam_service.Dto.PermissionDto;

@Data
public class PermissionListResponse {
    private List<PermissionDto> permissions;
}
