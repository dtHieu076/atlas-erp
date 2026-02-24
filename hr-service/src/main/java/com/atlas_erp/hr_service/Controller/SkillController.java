package com.atlas_erp.hr_service.Controller;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.atlas_erp.hr_service.Dto.Response.ApiResponse;
import com.atlas_erp.hr_service.Dto.Response.Skill.SkillDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Skill.SkillResponse;
import com.atlas_erp.hr_service.Service.SkillService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/skills")
@RequiredArgsConstructor
public class SkillController {
    private final SkillService skillService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getAllSkills() {
        List<SkillResponse> skills = skillService.getAllSkills();
        ApiResponse<List<SkillResponse>> response = ApiResponse.success(skills);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<SkillDetailResponse>> getSkillById(@PathVariable UUID id) {
        SkillDetailResponse skill = skillService.getSkillById(id);
        ApiResponse<SkillDetailResponse> response = ApiResponse.success(skill);
        return ResponseEntity.ok(response);
    }

}
