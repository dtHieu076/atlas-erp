package com.atlas_erp.iam_service.Dto.Request.Role;

import lombok.Data;
import java.util.List;

@Data
public class AssignPermissionsRequest {
    private List<String> permissionIds;
}
