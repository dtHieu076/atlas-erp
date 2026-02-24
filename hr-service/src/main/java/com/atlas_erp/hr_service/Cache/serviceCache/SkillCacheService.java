package com.atlas_erp.hr_service.Cache.serviceCache;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.atlas_erp.hr_service.Dto.Response.Skill.SkillDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Skill.SkillResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SkillCacheService {

    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration DEFAULT_CACHE_DURATION = Duration.ofMinutes(60);

    private static final String SKILL_LIST_KEY = "skill:list:all";
    private static final String SKILL_DETAIL_PREFIX = "skill:detail:";

    private String generateDetailKey(String skillId) {
        return SKILL_DETAIL_PREFIX + skillId;
    }

    // ===============================
    // LIST CACHE
    // ===============================
    public List<SkillResponse> getAllSkills() {

        Object data = redisTemplate.opsForValue().get(SKILL_LIST_KEY);

        if (data instanceof List<?>) {
            return ((List<?>) data)
                    .stream()
                    .filter(SkillResponse.class::isInstance)
                    .map(SkillResponse.class::cast)
                    .toList();
        }

        return null;
    }

    public void putAllSkills(List<SkillResponse> skills) {
        redisTemplate.opsForValue().set(
                SKILL_LIST_KEY,
                skills,
                DEFAULT_CACHE_DURATION);
    }

    public void evictAllSkills() {
        redisTemplate.delete(SKILL_LIST_KEY);
    }

    // ===============================
    // DETAIL CACHE
    // ===============================
    public SkillDetailResponse getSkillDetail(String skillId) {
        return (SkillDetailResponse) redisTemplate
                .opsForValue()
                .get(generateDetailKey(skillId));
    }

    public void putSkillDetail(String skillId, SkillDetailResponse detail) {
        redisTemplate.opsForValue().set(
                generateDetailKey(skillId),
                detail,
                DEFAULT_CACHE_DURATION);
    }

    public void evictSkillDetail(String skillId) {
        redisTemplate.delete(generateDetailKey(skillId));
    }
}