package com.atlas_erp.workflow_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "workflow_instance")
@Data
public class WorkflowInstanceEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID workflowId;

    private UUID businessId;
    private Integer currentStep;

    private String status;
}
