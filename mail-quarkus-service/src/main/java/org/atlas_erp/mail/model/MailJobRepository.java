package org.atlas_erp.mail.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;
import java.util.List;
import java.util.UUID;

/**
 * Repository for MailJob using Panache pattern.
 */
@ApplicationScoped
public class MailJobRepository implements PanacheRepository<MailJob> { // Chỉ giữ lại MailJob

    public List<MailJob> findPendingJobsByOldestFirst(int limit) {
        return find("status", MailJob.MailJobStatus.PENDING)
                .range(0, limit - 1)
                .list();
    }

    public void updateStatus(Long id, MailJob.MailJobStatus status, String error) {
        update("status = ?1, retryCount = retryCount + 1, errorMessage = ?2 WHERE id = ?3",
                status, error, id);
    }

    public long countByCampaignAndStatus(UUID campaignId, MailJob.MailJobStatus status) {
        return count("campaign.id = ?1 AND status = ?2", campaignId, status);
    }

    public long countByStatus(MailJob.MailJobStatus status) {
        return count("status = ?1", status);
    }
}