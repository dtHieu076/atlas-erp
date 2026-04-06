package org.atlas_erp.mail.service;

import org.atlas_erp.mail.dto.*;
import org.atlas_erp.mail.model.*;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.panache.common.Page;
import io.quarkus.panache.common.Sort;

/**
 * Campaign service for managing mail campaigns and statistics.
 */
@ApplicationScoped
public class CampaignService {

    @Inject
    MailCampaignRepository campaignRepository;

    public CampaignPageDTO getCampaigns(int page, int size) {
        var campaigns = campaignRepository.findAll(Sort.by("createdAt", Sort.Direction.Descending))
                .page(Page.of(page, size))
                .list();

        long totalElements = campaignRepository.count();
        int totalPages = (int) Math.ceil((double) totalElements / size);

        CampaignPageDTO dto = new CampaignPageDTO();
        dto.setContent(campaigns.stream().map(this::toDTO).toList());
        dto.setPage(page);
        dto.setSize(size);
        dto.setTotalElements(totalElements);
        dto.setTotalPages(totalPages);
        return dto;
    }

    public StatsDTO getStats() {
        StatsDTO stats = new StatsDTO();
        stats.setTotalSent(campaignRepository.count());
        stats.setTotalCampaigns((int) campaignRepository.count());
        stats.setPendingInQueue(campaignRepository.pendingJobsCount());
        stats.setSuccessRate(98.5);
        return stats;
    }

    public MailCampaign createCampaign(String templateId, String sendType, int recipientsCount) {
        MailCampaign campaign = new MailCampaign();
        campaign.setTemplateId(templateId);
        campaign.setSendType(sendType);
        campaign.setRecipientsCount(recipientsCount);
        campaign.setPendingCount(recipientsCount);
        campaignRepository.persist(campaign);
        return campaign;
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
