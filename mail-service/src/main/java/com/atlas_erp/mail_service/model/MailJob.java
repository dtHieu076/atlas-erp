package com.atlas_erp.mail_service.model;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * JPA Entity for mail_jobs table in PostgreSQL.
 * Tracks status for audit, retry, metrics.
 */
@Entity
@Table(name = "mail_jobs")
@Data
public class MailJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String templateId;

    @JdbcTypeCode(SqlTypes.JSON)
    @Column(name = "template_data", columnDefinition = "jsonb")
    private String templateData;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MailJobStatus status = MailJobStatus.PENDING;

    private int retryCount = 0;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    private String errorMessage;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "campaign_id")
    private MailCampaign campaign;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum MailJobStatus {
        PENDING, PROCESSING, SENT, FAILED, RETRYING, DEAD_LETTER
    }
}
