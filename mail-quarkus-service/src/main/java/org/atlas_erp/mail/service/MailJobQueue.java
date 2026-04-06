package org.atlas_erp.mail.service;

import org.atlas_erp.mail.dto.MailEventDTO;
import org.atlas_erp.mail.model.MailJob;
import org.atlas_erp.mail.model.MailJobRepository;
import org.atlas_erp.mail.model.MailCampaign;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.transaction.Transactional;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import java.util.logging.Logger;

/**
 * Queue service for enqueue/dequeue MailJobs from DB.
 * Batch processing with configurable limit.
 */
@ApplicationScoped
public class MailJobQueue {

    private static final Logger log = Logger.getLogger(MailJobQueue.class.getName());

    @Inject
    MailJobRepository mailJobRepository;

    @Inject
    CampaignService campaignService;

    private static final int BATCH_SIZE = 100;
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Enqueue MailEventDTO - create individual MailJob for each recipient.
     */
    @Transactional
    public void enqueue(MailEventDTO event) {
        // Create campaign
        MailCampaign campaign = campaignService.createCampaign(
                event.getTemplateId(),
                event.getSendType(),
                event.getRecipients().size());

        event.getRecipients().forEach(recipient -> {
            MailJob job = new MailJob();
            job.setRecipientEmail(recipient.getEmail());
            job.setTemplateId(event.getTemplateId());
            try {
                if (event.getTemplateData() != null) {
                    job.setTemplateData(mapper.writeValueAsString(event.getTemplateData()));
                } else {
                    job.setTemplateData("{}");
                }
            } catch (Exception e) {
                job.setTemplateData("{}");
            }
            job.setStatus(MailJob.MailJobStatus.PENDING);
            job.setCampaign(campaign);
            mailJobRepository.persist(job);
        });
        log.info("Enqueued " + event.getRecipients().size() + " mail jobs for campaign " + campaign.getId());
    }

    /**
     * Dequeue batch of PENDING jobs for worker processing.
     */
    public List<MailJob> dequeueBatch() {
        List<MailJob> batch = mailJobRepository.findPendingJobsByOldestFirst(BATCH_SIZE);
        if (!batch.isEmpty()) {
            // Mark as PROCESSING
            batch.forEach(job -> {
                job.setStatus(MailJob.MailJobStatus.PROCESSING);
                mailJobRepository.persist(job);
            });
        }
        return batch;
    }
}
