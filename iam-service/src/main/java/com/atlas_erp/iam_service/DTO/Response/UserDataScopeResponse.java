package com.atlas_erp.iam_service.DTO.Response;

import com.atlas_erp.iam_service.DTO.UserDataScopeDto;
import lombok.Data;
import java.util.List;

@Data
public class UserDataScopeResponse {
    private String userId;
    private List<UserDataScopeDto> dataScopes;
}
