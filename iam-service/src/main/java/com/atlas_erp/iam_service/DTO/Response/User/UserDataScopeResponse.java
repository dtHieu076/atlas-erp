package com.atlas_erp.iam_service.Dto.Response.User;

import lombok.Data;
import java.util.List;

import com.atlas_erp.iam_service.Dto.UserDataScopeDto;

@Data
public class UserDataScopeResponse {
    private String userId;
    private List<UserDataScopeDto> dataScopes;
}
