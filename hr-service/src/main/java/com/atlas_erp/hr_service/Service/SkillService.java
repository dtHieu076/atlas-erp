package com.atlas_erp.hr_service.Service;

import java.util.List;
import java.util.UUID;

import com.atlas_erp.hr_service.Dto.Response.Skill.SkillDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Skill.SkillResponse;

public interface SkillService {
    List<SkillResponse> getAllSkills();

    SkillDetailResponse getSkillById(UUID id);
}