package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "job_title", uniqueConstraints = {
        @UniqueConstraint(name = "uk_job_title_code", columnNames = "code")
}, indexes = {
        @Index(name = "idx_job_title_department", columnList = "department_id"),
        @Index(name = "idx_job_title_status", columnList = "status")
})
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobTitleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false, length = 50)
    private String code; // VD: DEV01, HRM02

    @Column(nullable = false, length = 255)
    private String name; // Tên chức danh

    @Column(name = "grade_level", length = 50)
    private String gradeLevel; // Junior, Senior, Manager...

    @Column(name = "department_id", nullable = false)
    private UUID departmentId; // reference Organization service

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(nullable = false, length = 20)
    private String status; // ACTIVE / INACTIVE

    @Column(name = "created_at", nullable = false)
    private Instant createdAt;

    @Column(name = "updated_at")
    private Instant updatedAt;

    @PrePersist
    public void prePersist() {
        this.createdAt = Instant.now();
        this.status = this.status == null ? "ACTIVE" : this.status;
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = Instant.now();
    }
}