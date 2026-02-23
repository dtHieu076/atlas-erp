package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "shift_scope")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftScopeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID shiftId;

    private UUID departmentId;
    private UUID teamId;

    @ManyToOne
    @JoinColumn(name = "shiftId", insertable = false, updatable = false)
    private ShiftEntity shift;
}