package com.atlas_erp.admin_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Cứu tinh vòng 2
public class VeleroBackupStatus {
    private String phase;
    private String startTimestamp;
    private String completionTimestamp;
    private String expiration;
    private Progress progress;

    @Data
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Progress {
        private Long itemsBackedUp;
        private Long totalItems;
    }
}