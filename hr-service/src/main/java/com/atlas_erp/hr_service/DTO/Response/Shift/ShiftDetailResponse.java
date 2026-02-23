package com.atlas_erp.hr_service.Dto.Response.Shift;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShiftDetailResponse {

    private String id;
    private String code;
    private String name;
    private String desc;
    private String time;
    private String totalHours;
    private String breakTime;
    private String status;

    private Timeline timeline;

    private String startTime;
    private String endTime;
    private String breakTimeValue;
    private String breakTimeDesc;

    private String lateAllowance;
    private String earlyAllowance;

    private String otRule;
    private String weekendRule;

    private List<String> appliedScope;

    private SysInfo sysInfo;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Timeline {
        private String start;
        private String width;
        private String breakStart;
        private String breakWidth;
        private Boolean isOvernight;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class SysInfo {
        private String roundedIn;
        private String roundedOut;
        private String autoCheckout;
        private String holidayRate;
    }
}