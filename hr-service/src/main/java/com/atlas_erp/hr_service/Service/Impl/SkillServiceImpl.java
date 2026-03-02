package com.atlas_erp.hr_service.Service.Impl;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.atlas_erp.hr_service.Cache.serviceCache.SkillCacheService;
import com.atlas_erp.hr_service.Dto.Response.Skill.SkillDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Skill.SkillResponse;
import com.atlas_erp.hr_service.Entity.SkillCriteriaEntity;
import com.atlas_erp.hr_service.Entity.SkillEntity;
import com.atlas_erp.hr_service.Repository.SkillCriteriaRepository;
import com.atlas_erp.hr_service.Repository.SkillRepository;
import com.atlas_erp.hr_service.Service.SkillService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class SkillServiceImpl implements SkillService {

        private final SkillRepository skillRepository;
        private final SkillCriteriaRepository skillCriteriaRepository;
        private final SkillCacheService skillCacheService;

        // ===============================
        // GET ALL
        // ===============================
        @Override
        public List<SkillResponse> getAllSkills() {

                // 1️⃣ Check cache trước
                List<SkillResponse> cached = skillCacheService.getAllSkills();
                if (cached != null && !cached.isEmpty()) {
                        return cached;
                }

                // 2️⃣ Query DB
                List<SkillResponse> skills = skillRepository.findAll()
                                .stream()
                                .map(skill -> SkillResponse.builder()
                                                .id(skill.getId())
                                                .code(skill.getCode())
                                                .name(skill.getName())
                                                .group(skill.getGroup().getName())
                                                .maxLevel(skill.getMaxLevel())
                                                .status(skill.getStatus())
                                                .build())
                                .collect(Collectors.toList());

                // 3️⃣ Put cache
                skillCacheService.putAllSkills(skills);

                return skills;
        }

        // ===============================
        // GET BY ID
        // ===============================
        @Override
        public SkillDetailResponse getSkillById(UUID id) {

                // 1️⃣ Check cache trước
                SkillDetailResponse cached = skillCacheService.getSkillDetail(id.toString());
                if (cached != null) {
                        return cached;
                }

                // 2️⃣ Query DB
                SkillEntity skill = skillRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Skill not found"));

                List<SkillCriteriaEntity> criteriaList = skillCriteriaRepository.findBySkillId(id);

                SkillDetailResponse response = SkillDetailResponse.builder()
                                .id(skill.getId())
                                .code(skill.getCode())
                                .name(skill.getName())
                                .group(skill.getGroup().getName())
                                .maxLevel(skill.getMaxLevel())
                                .description(skill.getDescription())
                                .status(skill.getStatus())
                                .createdAt(skill.getCreatedAt())
                                .updatedAt(skill.getUpdatedAt())
                                .updatedBy(skill.getUpdatedBy())
                                .criteria(criteriaList.stream()
                                                .map(c -> SkillDetailResponse.SkillCriteriaDto.builder()
                                                                .id(c.getId())
                                                                .title(c.getTitle())
                                                                .description(c.getDescription())
                                                                .build())
                                                .collect(Collectors.toList()))
                                .build();

                // 3️⃣ Put cache
                skillCacheService.putSkillDetail(id.toString(), response);

                return response;
        }
}