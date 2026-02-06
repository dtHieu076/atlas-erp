package com.atlas_erp.production_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.time.LocalDate;
import java.time.Instant;
import java.math.BigDecimal;

@Entity
@Table(name = "production_plan")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductionPlanEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String planCode;

    private Integer version;

    // --- Thông tin Sản phẩm & Số lượng ---
    private UUID productId;

    @Column(precision = 18, scale = 2)
    private BigDecimal quantityPlanned; // Tổng số lượng cần sản xuất theo kế hoạch

    @Column(precision = 18, scale = 2)
    private BigDecimal quantityAllocated; // Số lượng đã được giao vào các Production Order

    // --- Thông tin Tổ chức ---
    private UUID plantId;
    private UUID departmentId;

    // --- Thời gian ---
    private LocalDate startDate;
    private LocalDate endDate;

    // --- Trạng thái & Ghi chú ---
    private String status; // DRAFT, APPROVED, REJECTED, CLOSED

    @Column(columnDefinition = "TEXT")
    private String note;

    // --- Phê duyệt ---
    private UUID approvedBy;
    private Instant approvedAt;

    private Instant createdAt;
    private Instant updatedAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.updatedAt = Instant.now();
        if (this.quantityAllocated == null)
            this.quantityAllocated = BigDecimal.ZERO;
    }

    @PreUpdate
    protected void onUpdate() {
        this.updatedAt = Instant.now();
    }
}