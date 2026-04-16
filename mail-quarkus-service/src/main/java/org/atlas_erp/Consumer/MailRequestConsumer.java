package org.atlas_erp.Consumer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import org.atlas_erp.Entity.MailCampaign;
import org.atlas_erp.Entity.MailJob;
import org.atlas_erp.Repository.MailCampaignRepository;
import org.atlas_erp.Repository.MailJobRepository;
import org.atlas_erp.dto.MailEventDTO;
import org.eclipse.microprofile.reactive.messaging.Channel;
import org.eclipse.microprofile.reactive.messaging.Emitter;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.quarkus.logging.Log;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import jakarta.transaction.TransactionManager;
import jakarta.transaction.Status;

@ApplicationScoped
public class MailRequestConsumer {

    private final ObjectMapper objectMapper;
    private final MailJobRepository jobRepo;
    private final MailCampaignRepository campaignRepo;

    @Inject
    @Channel("internal-jobs-out")
    Emitter<String> jobEmitter; // 🔥 đổi sang String

    @Inject
    TransactionManager tm;

    public MailRequestConsumer(MailCampaignRepository campaignRepo,
            MailJobRepository jobRepo,
            ObjectMapper objectMapper) {
        this.campaignRepo = campaignRepo;
        this.jobRepo = jobRepo;
        this.objectMapper = objectMapper;
    }

    @Incoming("mail-requests")
    public void consume(MailEventDTO event) throws Exception {

        if (event == null || event.getRecipients() == null || event.getRecipients().isEmpty()) {
            Log.warn("Event không hợp lệ");
            return;
        }

        // 1. tạo campaign
        MailCampaign campaign = createCampaign(event);

        String jsonData = objectMapper.writeValueAsString(event.getTemplateData());

        int batchSize = 20;
        List<MailEventDTO.Recipient> recipients = event.getRecipients();

        // 2. chia batch
        for (int i = 0; i < recipients.size(); i += batchSize) {
            List<MailEventDTO.Recipient> batch = recipients.subList(i, Math.min(i + batchSize, recipients.size()));

            processBatch(batch, campaign, event, jsonData);
        }
    }

    // =========================
    // TRANSACTION METHODS
    // =========================

    @Transactional
    public MailCampaign createCampaign(MailEventDTO event) {
        MailCampaign campaign = new MailCampaign();
        campaign.setTemplateId(event.getTemplateId());
        campaign.setSendType(event.getSendType());
        campaign.setRecipientsCount(event.getRecipients().size());
        campaign.setPendingCount(event.getRecipients().size());

        campaignRepo.persist(campaign);
        return campaign;
    }

    @Transactional
    public void processBatch(List<MailEventDTO.Recipient> recipients,
            MailCampaign campaign,
            MailEventDTO event,
            String jsonData) {

        List<Long> jobIds = new ArrayList<>();

        Map<String, Object> baseData;
        try {
            baseData = objectMapper.readValue(
                    jsonData,
                    new TypeReference<Map<String, Object>>() {
                    });
        } catch (Exception e) {
            throw new RuntimeException("Parse templateData failed", e);
        }

        for (MailEventDTO.Recipient r : recipients) {

            // clone data cho từng user
            Map<String, Object> data = new HashMap<>(baseData);

            // inject userName
            data.put("userName", r.getName());

            MailJob job = new MailJob();
            job.setCampaign(campaign);
            job.setRecipientEmail(r.getEmail());
            job.setTemplateId(event.getTemplateId());

            try {
                job.setTemplateData(objectMapper.writeValueAsString(data));
            } catch (Exception e) {
                throw new RuntimeException("Serialize templateData failed", e);
            }

            job.setStatus(MailJob.MailJobStatus.PENDING);

            jobRepo.persist(job);
            jobIds.add(job.getId());
        }

        jobRepo.flush();

        // 🔥 emit sau commit
        try {
            tm.getTransaction().registerSynchronization(new jakarta.transaction.Synchronization() {

                @Override
                public void beforeCompletion() {
                }

                @Override
                public void afterCompletion(int status) {
                    if (status == Status.STATUS_COMMITTED) {
                        try {
                            String payload = objectMapper.writeValueAsString(jobIds);
                            jobEmitter.send(payload);
                            Log.infof("Emit batch %d jobs thành công", jobIds.size());
                        } catch (Exception e) {
                            Log.error("Lỗi serialize jobIds", e);
                        }
                    }
                }
            });

        } catch (Exception e) {
            Log.error("Lỗi register transaction sync", e);
        }
    }
}