package com.atlas_erp.mail_service.queue;

import com.atlas_erp.mail_service.dto.MailEventDTO;
import com.atlas_erp.mail_service.model.MailJob;
import com.atlas_erp.mail_service.model.MailJobRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.util.List;

/**
 * Queue service for enqueue/dequeue MailJobs from DB.
 * Batch processing with configurable limit.
 */
@Service
@RequiredArgsConstructor
public class MailJobQueue {

    private final MailJobRepository mailJobRepository;

    @Value("${app.mail.batch-size:100}")
    private int batchSize;

    /**
     * Enqueue MailEventDTO - create individual MailJob for each recipient.
     */
    public void enqueue(MailEventDTO event) {
        event.getRecipients().forEach(recipient -> {
            MailJob job = new MailJob();
            job.setRecipientEmail(recipient.getEmail());
            job.setTemplateId(event.getTemplateId());
            try {
                if (event.getTemplateData() != null) {
                    job.setTemplateData(new com.fasterxml.jackson.databind.ObjectMapper()
                            .writeValueAsString(event.getTemplateData()));
                } else {
                    job.setTemplateData("{}");
                }
            } catch (Exception e) {
                job.setTemplateData("{}");
            }
            job.setStatus(MailJob.MailJobStatus.PENDING);
            mailJobRepository.save(job);
        });
    }

    /**
     * Dequeue batch of PENDING jobs for worker processing.
     */
    public List<MailJob> dequeueBatch() {
        List<MailJob> batch = mailJobRepository.findPendingJobsByOldestFirst(batchSize);
        if (!batch.isEmpty()) {
            // Mark as PROCESSING
            batch.forEach(job -> {
                mailJobRepository.updateStatusWithRetry(job.getId(), MailJob.MailJobStatus.PROCESSING, null);
            });
        }
        return batch;
    }
}
