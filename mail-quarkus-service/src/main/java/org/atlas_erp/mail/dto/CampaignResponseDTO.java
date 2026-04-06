package org.atlas_erp.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
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
