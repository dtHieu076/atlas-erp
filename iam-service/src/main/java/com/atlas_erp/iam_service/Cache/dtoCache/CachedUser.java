package com.atlas_erp.iam_service.Cache.dtoCache;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.atlas_erp.iam_service.Entity.AppUserEntity;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CachedUser {
    private UUID userId;
    private String status;
    private UUID employeeId;

    public static CachedUser from(AppUserEntity entity) {
        return CachedUser.builder()
                .userId(entity.getId())
                .status(entity.getStatus())
                .employeeId(entity.getEmployeeId())
                .build();
    }
}
