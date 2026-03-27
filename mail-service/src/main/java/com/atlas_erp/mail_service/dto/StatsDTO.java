package com.atlas_erp.mail_service.dto;

import lombok.Data;

@Data
public class StatsDTO {
    private long totalSent;
    private double successRate;
    private int pendingInQueue;
    private int totalCampaigns;
}
