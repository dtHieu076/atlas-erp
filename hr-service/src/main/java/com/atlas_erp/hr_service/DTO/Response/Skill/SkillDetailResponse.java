package com.atlas_erp.hr_service.Dto.Response.Skill;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillDetailResponse {

    private UUID id;
    private String code;
    private String name;
    private String group;
    private Integer maxLevel;
    private String description;
    private String status;
    private Instant createdAt;
    private Instant updatedAt;
    private String updatedBy;
    private List<SkillCriteriaDto> criteria;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillCriteriaDto {
        private UUID id;
        private String title;
        private String description;
    }
}