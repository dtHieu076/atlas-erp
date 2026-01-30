package com.atlas_erp.iam_service.Service;

import com.atlas_erp.iam_service.Dto.Request.Permission.PermissionSearchRequest;
import com.atlas_erp.iam_service.Dto.Response.Permission.MyPermissionResponse;
import com.atlas_erp.iam_service.Dto.Response.Permission.PermissionListResponse;
import java.util.UUID;

public interface PermissionService {

    PermissionListResponse search(PermissionSearchRequest request);

    MyPermissionResponse getMyPermissions(UUID userId);
}
