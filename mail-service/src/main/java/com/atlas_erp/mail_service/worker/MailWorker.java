package com.atlas_erp.mail_service.worker;

import com.atlas_erp.mail_service.model.MailJob;
import com.atlas_erp.mail_service.queue.MailJobQueue;
import com.atlas_erp.mail_service.service.MailLoggerService;
import com.atlas_erp.mail_service.service.MailSenderService;
import com.atlas_erp.mail_service.service.TemplateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Worker that processes batch of MailJobs every 1s.
 * Render template, send email, update status.
 */
@Component
@RequiredArgsConstructor
@Slf4j
public class MailWorker {

    private final MailJobQueue jobQueue;
    private final TemplateService templateService;
    private final MailSenderService mailSenderService;
    private final MailLoggerService loggerService;

    @Value("${app.mail.max-retries:3}")
    private int maxRetries;

    @Scheduled(fixedDelay = 1000)
    public void processBatch() {
        List<MailJob> batch = jobQueue.dequeueBatch();
        if (batch.isEmpty()) {
            return;
        }

        log.info("Processing batch of {} jobs", batch.size());

        for (MailJob job : batch) {
            try {
                // 1. Render template (Thực hiện đồng bộ vì nó rất nhanh)
                String htmlContent = templateService.render(job);
                String subject = "Atlas ERP Notification - " + job.getTemplateId();

                // 2. Send email (Thực hiện BẤT ĐỒNG BỘ)
                mailSenderService.sendHtmlEmailAsync(job.getRecipientEmail(), subject, htmlContent)
                        .thenAccept(sent -> {
                            // Đoạn code này sẽ tự động chạy KHI EMAIL GỬI XONG
                            if (sent) {
                                loggerService.updateStatus(job, MailJob.MailJobStatus.SENT, null);
                            } else {
                                handleFailure(job);
                            }
                        })
                        .exceptionally(ex -> {
                            // Bắt lỗi nếu Thread Pool hoặc JavaMailSender văng Exception
                            log.error("Error sending job {}: {}", job.getId(), ex.getMessage());
                            handleFailure(job);
                            return null;
                        });

            } catch (Exception e) {
                // Bắt lỗi nếu lỗi xảy ra ở khâu render template
                log.error("Error rendering job {}: {}", job.getId(), e.getMessage());
                handleFailure(job);
            }
        }
        // Vòng lặp for kết thúc ngay lập tức, giải phóng Worker đi làm việc khác!
    }

    private void handleFailure(MailJob job) {
        if (job.getRetryCount() < maxRetries) {
            loggerService.updateStatus(job, MailJob.MailJobStatus.RETRYING, "Send failed, retrying");
        } else {
            loggerService.logDeadLetter(job);
        }
    }
}