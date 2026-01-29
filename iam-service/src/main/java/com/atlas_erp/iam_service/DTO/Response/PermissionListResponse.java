package com.atlas_erp.iam_service.DTO.Response;

import com.atlas_erp.iam_service.DTO.PermissionDto;
import lombok.Data;
import java.util.List;

@Data
public class PermissionListResponse {
    private List<PermissionDto> permissions;
}
