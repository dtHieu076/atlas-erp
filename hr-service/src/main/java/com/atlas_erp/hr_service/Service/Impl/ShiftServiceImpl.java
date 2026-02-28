package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Cache.serviceCache.ShiftCacheService;
import com.atlas_erp.hr_service.Dto.Response.Shift.ShiftDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Shift.ShiftResponse;
import com.atlas_erp.hr_service.Entity.ShiftEntity;
import com.atlas_erp.hr_service.Entity.ShiftRuleEntity;
import com.atlas_erp.hr_service.Entity.ShiftScopeEntity;
import com.atlas_erp.hr_service.Repository.ShiftRepository;
import com.atlas_erp.hr_service.Repository.ShiftRuleRepository;
import com.atlas_erp.hr_service.Repository.ShiftScopeRepository;
import com.atlas_erp.hr_service.Service.ShiftService;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ShiftServiceImpl implements ShiftService {

    private final ShiftRepository shiftRepository;
    private final ShiftRuleRepository shiftRuleRepository;
    private final ShiftScopeRepository shiftScopeRepository;
    private final ShiftCacheService shiftCacheService;

    // ===============================
    // GET ALL
    // ===============================
    @Override
    public List<ShiftResponse> getAllShifts() {
        // List<ShiftResponse> cachedShifts = shiftCacheService.getAllShifts();
        // if (!cachedShifts.isEmpty()) {
        //     return cachedShifts;
        // }

        List<ShiftResponse> shifts = shiftRepository.findAll()
                .stream()
                .map(this::mapToShiftResponse)
                .collect(Collectors.toList());

        // shiftCacheService.putAllShifts(shifts);

        return shifts;
    }

    // ===============================
    // GET BY ID
    // ===============================
    @Override
    public ShiftDetailResponse getShiftById(UUID id) {
        // Kiem tra cache truoc khi truy van DB
        // ShiftDetailResponse cachedDetail = shiftCacheService.getShiftDetail(id.toString());
        // if (cachedDetail != null) {
        //     return cachedDetail;
        // }

        // Truy vấn DB nếu không có trong cache
        ShiftEntity shift = shiftRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Shift not found with id: " + id));

        ShiftRuleEntity rule = shiftRuleRepository.findByShiftId(id).orElse(null);
        List<ShiftScopeEntity> scopes = shiftScopeRepository.findByShiftId(id);

        ShiftDetailResponse detail = mapToShiftDetailResponse(shift, rule, scopes);

        // Lưu vào cache sau khi truy vấn thành công
        // shiftCacheService.putShiftDetail(id.toString(), detail);

        return detail;
    }

    // =====================================================
    // MAPPER: ENTITY → SHIFT RESPONSE (LIST)
    // =====================================================
    private ShiftResponse mapToShiftResponse(ShiftEntity shift) {

        String timeRange = shift.getStartTime() + " - " + shift.getEndTime();

        int startMinutes = shift.getStartTime().toSecondOfDay() / 60;
        int endMinutes = shift.getEndTime().toSecondOfDay() / 60;

        boolean isOvernight = Boolean.TRUE.equals(shift.getIsOvernight());

        int durationMinutes;

        if (isOvernight) {
            durationMinutes = (1440 - startMinutes) + endMinutes;
        } else {
            durationMinutes = endMinutes - startMinutes;
        }

        int breakMinutes = shift.getBreakMinutes();
        int workingMinutes = durationMinutes - breakMinutes;

        // ==============================
        // TÍNH TIMELINE (%)
        // ==============================

        double startPercent = startMinutes / 1440.0 * 100;
        double widthPercent = durationMinutes / 1440.0 * 100;

        double breakStartMinute = startMinutes + (durationMinutes - breakMinutes) / 2.0;

        if (breakStartMinute >= 1440) {
            breakStartMinute -= 1440;
        }

        double breakStartPercent = breakStartMinute / 1440.0 * 100;

        double breakWidthPercent = breakMinutes / 1440.0 * 100;

        String totalHours = (workingMinutes / 60.0) + "h";

        return ShiftResponse.builder()
                .id(shift.getId().toString())
                .code(shift.getCode())
                .name(shift.getName())
                .time(timeRange)
                .totalHours(totalHours)
                .breakTime(breakMinutes + " phút")
                .status(shift.getStatus())
                .timeline(
                        ShiftResponse.Timeline.builder()
                                .start(round(startPercent))
                                .width(round(widthPercent))
                                .breakStart(round(breakStartPercent))
                                .breakWidth(round(breakWidthPercent))
                                .isOvernight(isOvernight)
                                .build())
                .build();
    }

    private String round(double value) {
        return String.format("%.2f", Math.round(value * 100.0) / 100.0);
    }

    // =====================================================
    // MAPPER: ENTITY → SHIFT DETAIL RESPONSE
    // =====================================================
    private ShiftDetailResponse mapToShiftDetailResponse(
            ShiftEntity shift,
            ShiftRuleEntity rule,
            List<ShiftScopeEntity> scopes) {

        List<String> appliedScope = scopes.stream()
                .map(scope -> scope.getDepartmentId().toString())
                .collect(Collectors.toList());

        ShiftDetailResponse.Timeline timeline = buildTimelineDetail(shift);
        String totalHours = calculateTotalHours(shift);

        return ShiftDetailResponse.builder()
                .id(shift.getId().toString())
                .code(shift.getCode())
                .name(shift.getName())
                .desc(shift.getDescription())
                .time(shift.getStartTime() + " - " + shift.getEndTime())
                .totalHours(totalHours)
                .breakTime(shift.getBreakMinutes() + " phút")
                .status(shift.getStatus())
                .timeline(timeline)

                .startTime(shift.getStartTime().toString())
                .endTime(shift.getEndTime().toString())
                .breakTimeValue(String.valueOf(shift.getBreakMinutes()))
                .breakTimeDesc(shift.getBreakMinutes() + " minutes")
                .lateAllowance(String.valueOf(shift.getLateAllowanceMinutes()))
                .earlyAllowance(String.valueOf(shift.getEarlyAllowanceMinutes()))

                .otRule(rule != null ? rule.getOtRule() : null)
                .weekendRule(rule != null ? rule.getWeekendRule() : null)

                .appliedScope(appliedScope)

                .sysInfo(
                        ShiftDetailResponse.SysInfo.builder()
                                .roundedIn(rule != null ? String.valueOf(rule.getRoundedInMinutes()) : null)
                                .roundedOut(rule != null ? String.valueOf(rule.getRoundedOutMinutes()) : null)
                                .autoCheckout(rule != null ? String.valueOf(rule.getAutoCheckout()) : null)
                                .holidayRate(rule != null ? String.valueOf(rule.getHolidayRate()) : null)
                                .build())
                .build();
    }

    private int calculateDurationMinutes(ShiftEntity shift) {

        int startMinutes = shift.getStartTime().toSecondOfDay() / 60;
        int endMinutes = shift.getEndTime().toSecondOfDay() / 60;

        boolean isOvernight = Boolean.TRUE.equals(shift.getIsOvernight());

        if (isOvernight) {
            return (1440 - startMinutes) + endMinutes;
        }

        return endMinutes - startMinutes;
    }

    private ShiftDetailResponse.Timeline buildTimelineDetail(ShiftEntity shift) {

        int startMinutes = shift.getStartTime().toSecondOfDay() / 60;
        int durationMinutes = calculateDurationMinutes(shift);
        int breakMinutes = shift.getBreakMinutes();

        double startPercent = startMinutes / 1440.0 * 100;
        double widthPercent = durationMinutes / 1440.0 * 100;

        double breakStartMinute = startMinutes + (durationMinutes - breakMinutes) / 2.0;

        if (breakStartMinute >= 1440) {
            breakStartMinute -= 1440;
        }

        double breakStartPercent = breakStartMinute / 1440.0 * 100;
        double breakWidthPercent = breakMinutes / 1440.0 * 100;

        return ShiftDetailResponse.Timeline.builder()
                .start(round(startPercent))
                .width(round(widthPercent))
                .breakStart(round(breakStartPercent))
                .breakWidth(round(breakWidthPercent))
                .isOvernight(Boolean.TRUE.equals(shift.getIsOvernight()))
                .build();
    }

    private String calculateTotalHours(ShiftEntity shift) {

        int durationMinutes = calculateDurationMinutes(shift);
        int workingMinutes = durationMinutes - shift.getBreakMinutes();

        double hours = workingMinutes / 60.0;

        return round(hours) + "h";
    }
}