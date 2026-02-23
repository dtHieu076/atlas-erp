package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "shift")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String code;

    @Column(nullable = false)
    private String name;

    private String description;

    @Column(nullable = false)
    private LocalTime startTime;

    @Column(nullable = false)
    private LocalTime endTime;

    private Integer breakMinutes;

    private Integer lateAllowanceMinutes;
    private Integer earlyAllowanceMinutes;

    private Boolean isOvernight;

    @Column(nullable = false)
    private String status; // ACTIVE / INACTIVE
}
