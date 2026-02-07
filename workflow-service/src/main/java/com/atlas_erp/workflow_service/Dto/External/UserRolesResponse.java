package com.atlas_erp.workflow_service.Dto.External;

import lombok.Data;
import java.util.List;

@Data
public class UserRolesResponse {
    private String userId;
    private List<RoleDto> roles;

    @Data
    public static class RoleDto {
        private String roleId;
        private String roleCode;
        private String roleName;
    }
}