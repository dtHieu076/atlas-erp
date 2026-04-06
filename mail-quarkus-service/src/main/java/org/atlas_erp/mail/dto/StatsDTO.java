package org.atlas_erp.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.quarkus.runtime.annotations.RegisterForReflection;

@Data
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class StatsDTO {
    private long totalSent;
    private double successRate;
    private int pendingInQueue;
    private int totalCampaigns;
}
