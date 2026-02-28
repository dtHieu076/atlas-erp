package com.atlas_erp.hr_service.Cache.serviceCache;

import com.atlas_erp.hr_service.Dto.Response.JobTitle.JobTitleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class JobTitleCacheService {
    private final RedisCacheService redisCacheService;

    private static final long TTL_MINUTES = 60;
    private static final String JOB_TITLE_LIST_KEY = "hr:job_title:list:all";

    public List<JobTitleResponse> getAllJobTitles() {
        return redisCacheService.getList(JOB_TITLE_LIST_KEY, JobTitleResponse.class);
    }

    public void putAllJobTitles(List<JobTitleResponse> jobTitles) {
        redisCacheService.put(JOB_TITLE_LIST_KEY, jobTitles, TTL_MINUTES);
    }

    public void evictAllJobTitles() {
        redisCacheService.delete(JOB_TITLE_LIST_KEY);
    }
}