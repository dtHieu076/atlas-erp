package com.atlas_erp.workflow_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "workflow")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class WorkflowEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private String name;
    private String businessType;

    // Bổ sung các trường còn thiếu
    private String description;
    private String status; // ACTIVE, INACTIVE
    private Integer version;
}