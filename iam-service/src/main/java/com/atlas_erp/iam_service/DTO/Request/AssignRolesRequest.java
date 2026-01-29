package com.atlas_erp.iam_service.DTO.Request;

import lombok.Data;
import java.util.List;

@Data
public class AssignRolesRequest {
    private List<String> roleIds;
}
