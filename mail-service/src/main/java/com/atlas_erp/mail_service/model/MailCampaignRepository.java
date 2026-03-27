package com.atlas_erp.mail_service.model;

import com.atlas_erp.mail_service.dto.CampaignPageDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface MailCampaignRepository extends JpaRepository<MailCampaign, UUID> {

    @Query("SELECT c FROM MailCampaign c ORDER BY c.createdAt DESC")
    Page<MailCampaign> findAllWithPagination(Pageable pageable);

    @Query("SELECT COUNT(c) FROM MailCampaign c")
    long countTotalCampaigns();

    @Query("SELECT SUM(CASE WHEN c.status = 'COMPLETED' OR c.status = 'FAILED' THEN c.recipientsCount ELSE 0 END) FROM MailCampaign c")
    long totalSent();

    @Query("SELECT COUNT(j) FROM MailJob j WHERE j.status = 'PENDING'")
    int pendingJobsCount();

}
