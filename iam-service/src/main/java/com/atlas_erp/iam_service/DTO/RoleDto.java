package com.atlas_erp.iam_service.Dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@AllArgsConstructor
@Getter
public class RoleDto {

    private UUID id;
    private String code;
    private String name;
    private String description;
    private String status;

    private List<UUID> permissionIds; // thêm
    private String dataScope;
}
