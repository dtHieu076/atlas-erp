package com.atlas_erp.organization_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.Instant;

@Entity
@Table(name = "department")
@Data
@Builder // Hỗ trợ tạo object nhanh: DepartmentEntity.builder().name("QC").build();
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentEntity {

    @Id
    @GeneratedValue
    private UUID id;

    @Column(nullable = false)
    private UUID plantId;

    @Column(nullable = false, length = 50)
    private String code;

    @Column(nullable = false, length = 255)
    private String name;

    private String costCenterCode;

    private LocalDate validFrom;
    private LocalDate validTo;

    // --- CÁC TRƯỜNG BỔ SUNG QUAN TRỌNG ---

    @Column(name = "created_at", updatable = false)
    private Instant createdAt; // Dùng Instant để chuẩn hóa múi giờ quốc tế (UTC)

    @Column(name = "created_by")
    private UUID createdBy; // Lưu userId người tạo để Audit

    @Column(name = "updated_at")
    private Instant updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    // Tự động gán thời gian khi insert/update
    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
    }
}