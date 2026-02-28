package com.atlas_erp.hr_service.Cache.serviceCache;

import java.util.List;

import org.springframework.stereotype.Service;

import com.atlas_erp.hr_service.Dto.Response.Skill.SkillDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Skill.SkillResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SkillCacheService {

    private final RedisCacheService redisCacheService;
    private static final long TTL_MINUTES = 60;

    private static final String SKILL_LIST_KEY = "hr:skill:list:all";
    private static final String SKILL_DETAIL_PREFIX = "hr:skill:detail:";

    private String generateDetailKey(String skillId) {
        return SKILL_DETAIL_PREFIX + skillId;
    }

    // ===============================
    // LIST CACHE
    // ===============================
    public List<SkillResponse> getAllSkills() {
        return redisCacheService.getList(SKILL_LIST_KEY, SkillResponse.class);
    }

    public void putAllSkills(List<SkillResponse> skills) {
        redisCacheService.put(SKILL_LIST_KEY, skills, TTL_MINUTES);
    }

    public void evictAllSkills() {
        redisCacheService.delete(SKILL_LIST_KEY);
    }

    // ===============================
    // DETAIL CACHE
    // ===============================
    public SkillDetailResponse getSkillDetail(String skillId) {
        return redisCacheService.get(generateDetailKey(skillId), SkillDetailResponse.class);
    }

    public void putSkillDetail(String skillId, SkillDetailResponse detail) {
        redisCacheService.put(generateDetailKey(skillId), detail, TTL_MINUTES);
    }

    public void evictSkillDetail(String skillId) {
        redisCacheService.delete(generateDetailKey(skillId));
    }
}