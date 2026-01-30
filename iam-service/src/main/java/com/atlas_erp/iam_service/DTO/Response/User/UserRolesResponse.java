package com.atlas_erp.iam_service.Dto.Response.User;

import lombok.Data;
import java.util.List;

import com.atlas_erp.iam_service.Dto.UserRoleDto;

@Data
public class UserRolesResponse {
    private String userId;
    private List<UserRoleDto> roles;
}
