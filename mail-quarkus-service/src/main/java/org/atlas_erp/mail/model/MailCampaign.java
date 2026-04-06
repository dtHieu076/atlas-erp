package org.atlas_erp.mail.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "mail_campaigns")
@Data
@NoArgsConstructor
@AllArgsConstructor
@RegisterForReflection
public class MailCampaign {

    @Id
    @GeneratedValue
    @Column(columnDefinition = "uuid")
    private UUID id;

    @Column(nullable = false)
    private String templateId;

    @Column(nullable = false)
    private String sendType; // "ALL", "TEAM", "INDIVIDUAL"

    private String name; // from template

    @Column(nullable = false)
    private int recipientsCount;

    private int successCount = 0;

    private int failedCount = 0;

    private int pendingCount;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MailCampaignStatus status = MailCampaignStatus.PROCESSING;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

    @OneToMany(mappedBy = "campaign", fetch = FetchType.LAZY)
    private List<MailJob> jobs;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        pendingCount = recipientsCount;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public enum MailCampaignStatus {
        PENDING, PROCESSING, COMPLETED, FAILED
    }
}
