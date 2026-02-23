package com.atlas_erp.hr_service.Cache.serviceCache;

import java.time.Duration;
import java.util.List;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import com.atlas_erp.hr_service.Dto.Response.Shift.ShiftDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Shift.ShiftResponse;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ShiftCacheService {
    private final RedisTemplate<String, Object> redisTemplate;
    private static final Duration DEFAULT_CACHE_DURATION = Duration.ofMinutes(60);

    private static final String SHIFT_LIST_KEY = "shift:list:all";
    private static final String SHIFT_DETAIL_PREFIX = "shift:detail:";

    private String generateDetailKey(String shiftId) {
        return SHIFT_DETAIL_PREFIX + shiftId;
    }

    // LIST CACHE
    public List<ShiftResponse> getAllShifts() {
        Object data = redisTemplate.opsForValue().get(SHIFT_LIST_KEY);

        if (data instanceof List<?>) {
            return ((List<?>) data)
                    .stream()
                    .filter(ShiftResponse.class::isInstance)
                    .map(ShiftResponse.class::cast)
                    .toList();
        }

        return null;
    }

    public void putAllShifts(List<ShiftResponse> shifts) {
        redisTemplate.opsForValue().set(
                SHIFT_LIST_KEY,
                shifts,
                DEFAULT_CACHE_DURATION);
    }

    public void evictAllShifts() {
        redisTemplate.delete(SHIFT_LIST_KEY);
    }

    // DETAIL CACHE
    public ShiftDetailResponse getShiftDetail(String shiftId) {
        return (ShiftDetailResponse) redisTemplate.opsForValue().get(generateDetailKey(shiftId));
    }

    public void putShiftDetail(String shiftId, ShiftDetailResponse detail) {
        redisTemplate.opsForValue().set(
                generateDetailKey(shiftId),
                detail,
                DEFAULT_CACHE_DURATION);
    }

    public void evictShiftDetail(String shiftId) {
        redisTemplate.delete(generateDetailKey(shiftId));
    }
}
