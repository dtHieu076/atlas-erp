package com.atlas_erp.iam_service.DTO.Response;

import com.atlas_erp.iam_service.DTO.UserSummaryDto;
import lombok.Data;
import java.util.List;

@Data
public class UserListResponse {
    private List<UserSummaryDto> users;
    private Long total;
}
