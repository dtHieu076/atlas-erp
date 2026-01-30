package com.atlas_erp.iam_service.Dto;

import lombok.Data;
import java.time.Instant;
import java.util.List;

@Data
public class UserSummaryDto {
    private String id;
    private String username;
    private String employeeId;
    private String status;
    private List<String> roles;
    private Instant createdAt;
}
