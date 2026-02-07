package com.atlas_erp.workflow_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;
import java.util.Date;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "workflow_instance")
@Data
@EntityListeners(AuditingEntityListener.class)
public class WorkflowInstanceEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID workflowId;

    private UUID businessId;
    private Integer currentStep;

    private String status;

    @CreatedDate // Tự động set khi insert
    @Column(updatable = false)
    private Date createdAt;

    @LastModifiedDate // Tự động update khi status thay đổi
    private Date updatedAt;
}
