package com.atlas_erp.iam_service.Dto.Response.User;

import lombok.Data;
import java.util.List;

import com.atlas_erp.iam_service.Dto.UserSummaryDto;

@Data
public class UserListResponse {
    private List<UserSummaryDto> users;
    private Long total;
}
