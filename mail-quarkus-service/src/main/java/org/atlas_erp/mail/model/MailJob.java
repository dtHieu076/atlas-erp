package org.atlas_erp.mail.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * JPA Entity for mail_jobs table in PostgreSQL.
 * Tracks status for audit, retry, metrics.
 */
@Entity
@Table(name = "mail_jobs")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class MailJob {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String recipientEmail;

    @Column(nullable = false)
    private String templateId;

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
