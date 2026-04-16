package org.atlas_erp.Consumer;

import java.util.ArrayList;
import java.util.List;

import org.atlas_erp.Entity.MailCampaign;
import org.atlas_erp.Entity.MailJob;
import org.atlas_erp.Repository.MailCampaignRepository;
import org.atlas_erp.Repository.MailJobRepository;
import org.atlas_erp.dto.MailEventDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MailRequestConsumer {
    private final ObjectMapper objectMapper;
    private final MailJobRepository jobRepo;
    private final MailCampaignRepository campaignRepo;

    @Inject
    @Channel("internal-jobs")
    Emitter<Long> jobEmitter;

    public MailRequestConsumer(MailCampaignRepository campaignRepo,
            MailJobRepository jobRepo,
            ObjectMapper objectMapper) {
        this.campaignRepo = campaignRepo;
        this.jobRepo = jobRepo;
        this.objectMapper = objectMapper;
    }

    @Incoming("mail-requests")
    @Transactional
    public void consume(MailEventDTO event) throws JsonProcessingException {
        // 1. Khởi tạo Campaign
        MailCampaign campaign = new MailCampaign();
        campaign.setTemplateId(event.getTemplateId());
        campaign.setSendType(event.getSendType());
        campaign.setRecipientsCount(event.getRecipients().size());
        campaignRepo.persist(campaign);

        String jsonData = objectMapper.writeValueAsString(event.getTemplateData());

        // Danh sách tạm để giữ ID
        List<Long> jobIds = new ArrayList<>();

        // 2. Lưu tất cả Jobs vào DB trước
        for (MailEventDTO.Recipient recipient : event.getRecipients()) {
            MailJob job = new MailJob();
            job.setCampaign(campaign);
            job.setRecipientEmail(recipient.getEmail());
            job.setTemplateId(event.getTemplateId());
            job.setTemplateData(jsonData);
            job.setStatus(MailJob.MailJobStatus.PENDING);

            jobRepo.persist(job);
            jobRepo.flush();

            jobIds.add(job.getId());
        }

        // 3. Sau khi vòng lặp kết thúc, transaction chuẩn bị commit.
        jobIds.forEach(id -> jobEmitter.send(id));
    }
}
