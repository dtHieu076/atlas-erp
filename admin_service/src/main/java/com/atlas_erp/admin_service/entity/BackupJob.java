package com.atlas_erp.admin_service.entity;

import com.atlas_erp.admin_service.enums.BackupStatus;

import jakarta.persistence.*;
import jakarta.persistence.GenerationType;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

@Entity
@Table(name = "backup_jobs")
public class BackupJob {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String veleroBackupName;

    private String targetNamespace;

    @Enumerated(EnumType.STRING)
    private BackupStatus status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    public BackupJob() {
    }

    public BackupJob(String targetNamespace, BackupStatus status) {
        this.targetNamespace = targetNamespace;
        this.status = status;
    }

    // Getters and Setters
    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getVeleroBackupName() {
        return veleroBackupName;
    }

    public void setVeleroBackupName(String veleroBackupName) {
        this.veleroBackupName = veleroBackupName;
    }

    public String getTargetNamespace() {
        return targetNamespace;
    }

    public void setTargetNamespace(String targetNamespace) {
        this.targetNamespace = targetNamespace;
    }

    public BackupStatus getStatus() {
        return status;
    }

    public void setStatus(BackupStatus status) {
        this.status = status;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
