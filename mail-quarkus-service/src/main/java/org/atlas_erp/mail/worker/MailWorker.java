package org.atlas_erp.mail.worker;

import org.atlas_erp.mail.model.MailJob;
import org.atlas_erp.mail.service.MailJobQueue;
import org.atlas_erp.mail.service.MailLoggerService;
import org.atlas_erp.mail.service.MailSenderService;
import org.atlas_erp.mail.service.TemplateService;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.scheduler.Scheduled;
import java.util.List;
import java.util.logging.Logger;

/**
 * Worker that processes batch of MailJobs every 1 second.
 * Render template, send email, update status.
 */
@ApplicationScoped
public class MailWorker {

    private static final Logger log = Logger.getLogger(MailWorker.class.getName());
    private static final int MAX_RETRIES = 3;

    @Inject
    MailJobQueue jobQueue;

    @Inject
    TemplateService templateService;

    @Inject
    MailSenderService mailSenderService;

    @Inject
    MailLoggerService loggerService;

    /**
     * Process batch of pending mail jobs every 1 second.
     */
    @Scheduled(every = "1s")
    public void processBatch() {
        List<MailJob> batch = jobQueue.dequeueBatch();
        if (batch.isEmpty()) {
            return;
        }

        log.info("Processing batch of " + batch.size() + " jobs");

        for (MailJob job : batch) {
            try {
                // 1. Render template
                String htmlContent = templateService.render(job);
                String subject = "Atlas ERP Notification - " + job.getTemplateId();

                // 2. Send email asynchronously (non-blocking)
                mailSenderService.sendHtmlEmailAsync(job.getRecipientEmail(), subject, htmlContent)
                        .subscribe()
                        .with(
                                sent -> {
                                    if (sent) {
                                        loggerService.updateStatus(job, MailJob.MailJobStatus.SENT, null);
                                    } else {
                                        handleFailure(job);
                                    }
                                },
                                error -> {
                                    log.severe("Error sending job " + job.getId() + ": " + error.getMessage());
                                    handleFailure(job);
                                });

            } catch (Exception e) {
                log.severe("Error rendering job " + job.getId() + ": " + e.getMessage());
                handleFailure(job);
            }
        }
        // Batch processing completes immediately, worker is freed for other tasks
    }

    private void handleFailure(MailJob job) {
        if (job.getRetryCount() < MAX_RETRIES) {
            loggerService.updateStatus(job, MailJob.MailJobStatus.RETRYING, "Send failed, retrying");
        } else {
            loggerService.logDeadLetter(job);
        }
    }
}
