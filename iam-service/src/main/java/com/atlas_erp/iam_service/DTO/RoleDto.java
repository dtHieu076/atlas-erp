package com.atlas_erp.iam_service.Dto;

import lombok.Data;
import com.atlas_erp.iam_service.Entity.RoleEntity;

@Data
public class RoleDto {

    private String id;
    private String code;
    private String name;
    private String description;

    public static RoleDto fromEntity(RoleEntity entity) {
        RoleDto dto = new RoleDto();
        dto.setId(entity.getId().toString());
        dto.setCode(entity.getRoleCode());
        dto.setName(entity.getRoleName());
        dto.setDescription(null); // chưa có trong DB
        return dto;
    }
}
