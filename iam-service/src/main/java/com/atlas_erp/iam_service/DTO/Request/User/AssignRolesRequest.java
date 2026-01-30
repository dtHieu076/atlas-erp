package com.atlas_erp.iam_service.Dto.Request.User;

import lombok.Data;
import java.util.List;

@Data
public class AssignRolesRequest {
    private List<String> roleIds;
}
