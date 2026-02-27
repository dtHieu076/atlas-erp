package com.atlas_erp.hr_service.Cache.serviceCache;

import com.atlas_erp.hr_service.Dto.Response.JobTitle.JobTitleResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

@Service
@RequiredArgsConstructor
public class JobTitleCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration DEFAULT_CACHE_DURATION = Duration.ofMinutes(60);

    private static final String JOB_TITLE_LIST_KEY = "job_title:list:all";

    // Lấy toàn bộ danh sách từ Cache
    public List<JobTitleResponse> getAllJobTitles() {
        Object data = redisTemplate.opsForValue().get(JOB_TITLE_LIST_KEY);

        if (data instanceof List<?>) {
            return ((List<?>) data)
                    .stream()
                    .filter(JobTitleResponse.class::isInstance)
                    .map(JobTitleResponse.class::cast)
                    .toList();
        }
        return null;
    }

    // Lưu danh sách vào Cache
    public void putAllJobTitles(List<JobTitleResponse> jobTitles) {
        redisTemplate.opsForValue().set(
                JOB_TITLE_LIST_KEY,
                jobTitles,
                DEFAULT_CACHE_DURATION);
    }

    // Xóa Cache (Dùng khi có thêm/sửa/xóa JobTitle)
    public void evictAllJobTitles() {
        redisTemplate.delete(JOB_TITLE_LIST_KEY);
    }
}