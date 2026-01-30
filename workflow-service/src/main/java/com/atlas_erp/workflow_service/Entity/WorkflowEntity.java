package com.atlas_erp.workflow_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "workflow")
@Data
public class WorkflowEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private String name;
    private String businessType;
}
