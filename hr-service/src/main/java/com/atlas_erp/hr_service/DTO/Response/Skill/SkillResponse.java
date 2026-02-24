package com.atlas_erp.hr_service.Dto.Response.Skill;

import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillResponse {

    private UUID id;
    private String code;
    private String name;
    private String group;
    private Integer maxLevel;
    private String status;
}