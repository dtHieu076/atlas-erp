package com.atlas_erp.hr_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "shift_rule")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ShiftRuleEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(nullable = false)
    private UUID shiftId;

    @Column(nullable = false)
    private String otRule;

    private String weekendRule;

    private Integer roundedInMinutes;
    private Integer roundedOutMinutes;

    private Boolean autoCheckout;

    private BigDecimal holidayRate;

    @OneToOne
    @JoinColumn(name = "shiftId", insertable = false, updatable = false)
    private ShiftEntity shift;
}