package com.atlas_erp.iam_service.Dto.Response.Role;

import lombok.Data;
import java.util.List;

import com.atlas_erp.iam_service.Dto.RoleDto;

@Data
public class RoleListResponse {
    private List<RoleDto> roles;
}
