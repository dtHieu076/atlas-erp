package com.atlas_erp.hr_service.Dto.Response.Employee;

import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillResponse {

    private UUID skillId;
    private String skillName;
    private Integer level;
    private Boolean certified;
    private LocalDate certifiedDate;
}
