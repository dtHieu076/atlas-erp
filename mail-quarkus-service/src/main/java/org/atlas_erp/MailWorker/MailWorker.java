package org.atlas_erp.MailWorker;

import java.util.logging.Logger;

import org.atlas_erp.Entity.MailJob;
import org.atlas_erp.Repository.MailCampaignRepository;
import org.atlas_erp.Repository.MailJobRepository;
import org.atlas_erp.Service.TemplateService;
import org.eclipse.microprofile.reactive.messaging.Incoming;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.Mailer;
import io.smallrye.reactive.messaging.annotations.Blocking;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class MailWorker {
    private static final Logger LOG = Logger.getLogger(MailWorker.class);
    private static final String FROM_EMAIL = "DangThanhHieu@atlas-erp.com";

    private final MailJobRepository jobRepo;
    private final MailCampaignRepository campaignRepo;
    private final TemplateService templateService;
    private final Mailer mailer;

    public MailWorker(MailJobRepository jobRepo, MailCampaignRepository campaignRepo,
            TemplateService templateService, Mailer mailer) {
        this.jobRepo = jobRepo;
        this.campaignRepo = campaignRepo;
        this.templateService = templateService;
        this.mailer = mailer;
    }

    @Incoming("internal-jobs")
    @Blocking(ordered = false)
    @Transactional
    public void process(Long jobId) {
        MailJob job = jobRepo.findById(jobId);
        if (job == null || job.getStatus() != MailJob.MailJobStatus.PENDING)
            return;

        try {
            job.setStatus(MailJob.MailJobStatus.PROCESSING);

            /// 2. Render Template
            String htmlContent = templateService.render(job);
            String subject = "Atlas ERP Notification - " + job.getTemplateId();

            // 3. Gửi Mail (Quarkus Mailer sẽ gửi tuần tự trong thread này, nhưng nhiều
            // thread chạy song song)
            mailer.send(Mail.withHtml(job.getRecipientEmail(), subject, htmlContent)
                    .setFrom(FROM_EMAIL));

            LOG.infof("Email sent successfully to %s", job.getRecipientEmail());

            // 4. Cập nhật thành công
            job.setStatus(MailJob.MailJobStatus.SENT);
            updateCampaignStats(job, true);

        } catch (Exception e) {
            LOG.errorf("Error sending job %d: %s", job.getId(), e.getMessage());
            handleFailure(job, e.getMessage());
        }
    }

    private void handleFailure(MailJob job, String errorMsg) {
        int maxRetries = 3; // Lấy từ config nếu muốn
        if (job.getRetryCount() < maxRetries) {
            job.setStatus(MailJob.MailJobStatus.RETRYING);
            job.setRetryCount(job.getRetryCount() + 1);
        } else {
            job.setStatus(MailJob.MailJobStatus.FAILED);
            updateCampaignStats(job, false);
        }
        job.setErrorMessage(errorMsg);
    }

    private void updateCampaignStats(MailJob job, boolean isSuccess) {
        java.util.UUID campaignId = job.getCampaign().getId();
        if (isSuccess) {
            campaignRepo.update("successCount = successCount + 1, pendingCount = pendingCount - 1 WHERE id = ?1",
                    campaignId);
        } else {
            campaignRepo.update("failedCount = failedCount + 1, pendingCount = pendingCount - 1 WHERE id = ?1",
                    campaignId);
        }
        campaignRepo.update("status = 'COMPLETED' WHERE id = ?1 AND pendingCount <= 0", campaignId);
    }
}
