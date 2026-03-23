package com.atlas_erp.mail_service.service;

import com.atlas_erp.mail_service.model.MailJob;
import com.atlas_erp.mail_service.model.MailJobRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

/**
 * Service to update MailJob status for audit/retry.
 */
@Service
@RequiredArgsConstructor
@Slf4j
public class MailLoggerService {

    private final MailJobRepository mailJobRepository;

    public void updateStatus(MailJob job, MailJob.MailJobStatus status, String error) {
        job.setStatus(status);
        job.setErrorMessage(error);
        if (status == MailJob.MailJobStatus.FAILED || status == MailJob.MailJobStatus.RETRYING) {
            job.setRetryCount(job.getRetryCount() + 1);
        }
        mailJobRepository.save(job);
        log.info("Job {} updated to {} (retries={})", job.getId(), status, job.getRetryCount());
    }

    public void logDeadLetter(MailJob job) {
        job.setStatus(MailJob.MailJobStatus.DEAD_LETTER);
        mailJobRepository.save(job);
        log.error("Job {} moved to DEAD_LETTER: {}", job.getId(), job.getErrorMessage());
    }
}
