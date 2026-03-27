package com.atlas_erp.mail_service.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
public class CampaignResponseDTO {
    private UUID id;
    private String templateId;
    private String sendType;
    private int recipientsCount;
    private int successCount;
    private int failedCount;
    private int pendingCount;
    private String status;
    private LocalDateTime createdAt;
}
