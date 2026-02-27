package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Cache.serviceCache.JobTitleCacheService;
import com.atlas_erp.hr_service.Dto.Response.JobTitle.JobTitleResponse;
import com.atlas_erp.hr_service.Entity.JobTitleEntity;
import com.atlas_erp.hr_service.Repository.JobTitleRepository;
import com.atlas_erp.hr_service.Service.JobTitleService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; // Thêm log để kiểm tra cho dễ
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class JobTitleServiceImpl implements JobTitleService {

    private final JobTitleRepository jobTitleRepository;
    private final JobTitleCacheService jobTitleCacheService;

    @Override
    public List<JobTitleResponse> getAllJobTitles() {
        // 1. Kiểm tra trong Cache trước
        List<JobTitleResponse> cachedData = jobTitleCacheService.getAllJobTitles();
        if (cachedData != null) {
            log.info("Lấy danh sách JobTitle từ Redis Cache");
            return cachedData;
        }

        // 2. Nếu Cache trống, truy vấn Database
        log.info("Cache trống, truy vấn Database");
        List<JobTitleEntity> entities = jobTitleRepository.findAll();

        List<JobTitleResponse> responses = entities.stream()
                .map(this::mapToResponse)
                .toList();

        // 3. Lưu kết quả vào Cache cho lần sau
        jobTitleCacheService.putAllJobTitles(responses);

        return responses;
    }

    private JobTitleResponse mapToResponse(JobTitleEntity entity) {
        return JobTitleResponse.builder()
                .code(entity.getCode())
                .name(entity.getName())
                .level(entity.getGradeLevel())
                .dept(entity.getDepartmentId() != null
                        ? entity.getDepartmentId().toString()
                        : null)
                .departmentName(entity.getDepartmentName())
                .desc(entity.getDescription())
                .status(entity.getStatus())
                .build();
    }
}