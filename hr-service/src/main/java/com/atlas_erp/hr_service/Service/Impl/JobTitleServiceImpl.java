package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Dto.Response.JobTitle.JobTitleResponse;
import com.atlas_erp.hr_service.Entity.JobTitleEntity;
import com.atlas_erp.hr_service.Repository.JobTitleRepository;
import com.atlas_erp.hr_service.Service.JobTitleService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class JobTitleServiceImpl implements JobTitleService {

    private final JobTitleRepository jobTitleRepository;

    @Override
    public List<JobTitleResponse> getAllJobTitles() {

        List<JobTitleEntity> entities = jobTitleRepository.findAll();

        return entities.stream()
                .map(this::mapToResponse)
                .toList();
    }

    private JobTitleResponse mapToResponse(JobTitleEntity entity) {

        return JobTitleResponse.builder()
                .code(entity.getCode())
                .name(entity.getName())
                .level(entity.getGradeLevel())
                .dept(entity.getDepartmentId() != null
                        ? entity.getDepartmentId().toString()
                        : null) // tạm thời trả UUID string
                .desc(entity.getDescription())
                .status(entity.getStatus())
                .build();
    }
}
