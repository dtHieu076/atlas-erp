package com.atlas_erp.workflow_service.Dto.External;

import lombok.Data;
import java.util.List;

@Data
public class RoleListResponse {
    private List<RoleDto> roles;

    @Data
    public static class RoleDto {
        private String id;
        private String code;
        private String name;
        private String description;
    }
}