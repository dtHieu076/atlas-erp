package org.atlas_erp.mail.service;

import org.atlas_erp.mail.model.MailJob;
import org.atlas_erp.mail.model.MailJobRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.logging.Logger;

/**
 * Service to update MailJob status for audit/retry.
 */
@ApplicationScoped
public class MailLoggerService {

    private static final Logger log = Logger.getLogger(MailLoggerService.class.getName());

    @Inject
    MailJobRepository mailJobRepository;

    public void updateStatus(MailJob job, MailJob.MailJobStatus status, String error) {
        job.setStatus(status);
        job.setErrorMessage(error);
        if (status == MailJob.MailJobStatus.FAILED || status == MailJob.MailJobStatus.RETRYING) {
            job.setRetryCount(job.getRetryCount() + 1);
        }
        mailJobRepository.persist(job);
        log.info("Job " + job.getId() + " updated to " + status + " (retries=" + job.getRetryCount() + ")");
    }

    public void logDeadLetter(MailJob job) {
        job.setStatus(MailJob.MailJobStatus.DEAD_LETTER);
        mailJobRepository.persist(job);
        log.severe("Job " + job.getId() + " moved to DEAD_LETTER: " + job.getErrorMessage());
    }
}
