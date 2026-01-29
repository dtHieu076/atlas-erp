package com.atlas_erp.iam_service.DTO.Response;

import com.atlas_erp.iam_service.DTO.RoleDto;
import lombok.Data;
import java.util.List;

@Data
public class RoleListResponse {
    private List<RoleDto> roles;
}
