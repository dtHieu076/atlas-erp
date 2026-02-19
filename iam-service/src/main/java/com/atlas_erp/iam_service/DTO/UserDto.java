package com.atlas_erp.iam_service.Dto;

import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserDto {

    private UUID id;
    private UUID employeeId;
    private String username;
    private String status;
    private Instant createdAt;
}
