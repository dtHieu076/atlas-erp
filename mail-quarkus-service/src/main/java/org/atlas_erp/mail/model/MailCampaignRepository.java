package org.atlas_erp.mail.model;

import io.quarkus.hibernate.orm.panache.PanacheRepository;
import jakarta.enterprise.context.ApplicationScoped;

/**
 * Repository for MailCampaign using Panache pattern.
 */
@ApplicationScoped
public class MailCampaignRepository implements PanacheRepository<MailCampaign> { // Chỉ giữ lại MailCampaign

    public long totalSent() {
        return count(); // Hết lỗi
    }

    public int pendingJobsCount() {
        // Hết lỗi getEntityManager()
        Long count = getEntityManager()
                .createQuery("SELECT COUNT(j) FROM MailJob j WHERE j.status = 'PENDING'", Long.class)
                .getSingleResult();
        return count.intValue();
    }
}