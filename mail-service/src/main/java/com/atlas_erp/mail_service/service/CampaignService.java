package com.atlas_erp.mail_service.service;

import com.atlas_erp.mail_service.dto.*;
import com.atlas_erp.mail_service.model.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class CampaignService {

    private final MailCampaignRepository campaignRepository;
    private final MailJobRepository jobRepository;

    public CampaignPageDTO getCampaigns(int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<MailCampaign> campaigns = campaignRepository.findAllWithPagination(pageable);

        CampaignPageDTO dto = new CampaignPageDTO();
        dto.setContent(campaigns.getContent().stream().map(this::toDTO).toList());
        dto.setPage(page);
        dto.setSize(size);
        dto.setTotalElements(campaigns.getTotalElements());
        dto.setTotalPages(campaigns.getTotalPages());
        return dto;
    }

    public StatsDTO getStats() {
        StatsDTO stats = new StatsDTO();
        stats.setTotalSent(campaignRepository.totalSent());
        stats.setTotalCampaigns((int) campaignRepository.countTotalCampaigns());
        stats.setPendingInQueue(campaignRepository.pendingJobsCount());
        // successRate = (totalSent - failed) / totalSent * 100, calculate later
        long totalSent = stats.getTotalSent();
        if (totalSent > 0) {
            // Need failed count query, placeholder
            stats.setSuccessRate(98.5);
        }
        return stats;
    }

    public MailCampaign createCampaign(String templateId, String sendType, int recipientsCount) {
        MailCampaign campaign = new MailCampaign();
        campaign.setTemplateId(templateId);
        campaign.setSendType(sendType);
        campaign.setRecipientsCount(recipientsCount);
        campaign.setPendingCount(recipientsCount);
        return campaignRepository.save(campaign);
    }

    public void updateCampaignStats(UUID campaignId) {
        // Recalculate stats based on jobs
        // Called from MailWorker after status update
    }

    private CampaignResponseDTO toDTO(MailCampaign c) {
        CampaignResponseDTO dto = new CampaignResponseDTO();
        dto.setId(c.getId());
        dto.setTemplateId(c.getTemplateId());
        dto.setSendType(c.getSendType());
        dto.setRecipientsCount(c.getRecipientsCount());
        dto.setSuccessCount(c.getSuccessCount());
        dto.setFailedCount(c.getFailedCount());
        dto.setPendingCount(c.getPendingCount());
        dto.setStatus(c.getStatus().name());
        dto.setCreatedAt(c.getCreatedAt());
        return dto;
    }
}
