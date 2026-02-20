package com.atlas_erp.workflow_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "workflow_step")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowStepEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "workflow_id", nullable = false)
    private UUID workflowId;

    @Column(name = "name", nullable = false, length = 255)
    private String name; // Cột name đã được gắn thẻ đúng chuẩn

    @Column(name = "step_order", nullable = false)
    private Integer stepOrder;

    @Column(name = "role_id", nullable = false)
    private UUID roleId;

    @Column(name = "action_type", nullable = false, length = 20)
    private String actionType; // APPROVE | REVIEW

    @Column(name = "condition_rule", columnDefinition = "TEXT")
    private String conditionRule;

    @Column(name = "duration_hours")
    private Integer durationHours;
}