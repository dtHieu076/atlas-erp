package com.atlas_erp.iam_service.DTO.Response;

import com.atlas_erp.iam_service.DTO.UserRoleDto;
import lombok.Data;
import java.util.List;

@Data
public class UserRolesResponse {
    private String userId;
    private List<UserRoleDto> roles;
}
