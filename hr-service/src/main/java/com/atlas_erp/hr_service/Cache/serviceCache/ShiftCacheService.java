package com.atlas_erp.hr_service.Cache.serviceCache;

import com.atlas_erp.hr_service.Dto.Response.Shift.ShiftDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Shift.ShiftResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ShiftCacheService {
    private final RedisCacheService redisCacheService;

    private static final long TTL_MINUTES = 60;
    private static final String SHIFT_LIST_KEY = "hr:shift:list:all";
    private static final String SHIFT_DETAIL_PREFIX = "hr:shift:detail:";

    private String generateDetailKey(String shiftId) {
        return SHIFT_DETAIL_PREFIX + shiftId;
    }

    public List<ShiftResponse> getAllShifts() {
        return redisCacheService.getList(SHIFT_LIST_KEY, ShiftResponse.class);
    }

    public void putAllShifts(List<ShiftResponse> shifts) {
        redisCacheService.put(SHIFT_LIST_KEY, shifts, TTL_MINUTES);
    }

    public void evictAllShifts() {
        redisCacheService.delete(SHIFT_LIST_KEY);
    }

    public ShiftDetailResponse getShiftDetail(String shiftId) {
        return redisCacheService.get(generateDetailKey(shiftId), ShiftDetailResponse.class);
    }

    public void putShiftDetail(String shiftId, ShiftDetailResponse detail) {
        redisCacheService.put(generateDetailKey(shiftId), detail, TTL_MINUTES);
    }

    public void evictShiftDetail(String shiftId) {
        redisCacheService.delete(generateDetailKey(shiftId));
    }
}
