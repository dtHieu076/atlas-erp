package com.atlas_erp.hr_service.Dto.Response.Employee;

import lombok.*;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ManagerDTO {
    private UUID id;
    private String fullName;
    private String jobTitleName;
}
