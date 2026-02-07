package com.atlas_erp.workflow_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "workflow_step")
@Data
public class WorkflowStepEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID workflowId;
    private Integer stepOrder;

    private UUID roleId;

    // Bổ sung trường còn thiếu
    private String actionType; // APPROVE | REVIEW

    private String conditionRule;
    private Integer durationHours;
}