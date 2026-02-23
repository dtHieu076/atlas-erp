package com.atlas_erp.hr_service.Dto.Response.Shift;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShiftResponse {

    private String id;
    private String code;
    private String name;
    private String time;
    private String totalHours;
    private String breakTime;
    private String status;
    private Timeline timeline;

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
}
