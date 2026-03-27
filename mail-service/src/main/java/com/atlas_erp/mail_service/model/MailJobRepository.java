package com.atlas_erp.mail_service.model;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.atlas_erp.mail_service.model.MailJob.MailJobStatus;

import java.util.List;
import java.util.UUID;

/**
 * Repository for MailJob CRUD and custom queries for queue operations.
 */
@Repository
public interface MailJobRepository extends JpaRepository<MailJob, Long> {

    // SỬA LỖI 1: Chuyển sang Native Query để dùng được LIMIT của PostgreSQL
    @Query(value = "SELECT * FROM mail_jobs WHERE status = 'PENDING' ORDER BY created_at ASC LIMIT :limit", nativeQuery = true)
    List<MailJob> findPendingJobsByOldestFirst(@Param("limit") int limit);

    // SỬA LỖI 2: Thêm @Modifying, @Transactional và @Param cho các biến
    @Transactional
    @Modifying
    @Query("UPDATE MailJob j SET j.status = :status, j.retryCount = j.retryCount + 1, j.errorMessage = :error WHERE j.id = :id")
    int updateStatusWithRetry(@Param("id") Long id,
            @Param("status") MailJob.MailJobStatus status,
            @Param("error") String error);

    // Campaign queries
    @Query("SELECT COUNT(j) FROM MailJob j WHERE j.campaign.id = :campaignId AND j.status = :status")
    long countByCampaignAndStatus(@Param("campaignId") UUID campaignId, @Param("status") MailJobStatus status);

    @Query("SELECT COUNT(j) FROM MailJob j WHERE j.status = :status")
    long countByStatus(@Param("status") MailJobStatus status);
}
