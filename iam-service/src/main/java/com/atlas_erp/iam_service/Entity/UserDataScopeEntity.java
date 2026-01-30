package com.atlas_erp.iam_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "user_data_scope")
@Data
public class UserDataScopeEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID userId;

    @Enumerated(EnumType.STRING)
    private ScopeType scopeType;

    private UUID scopeId;

    public enum ScopeType {
        PLANT, DEPARTMENT, WAREHOUSE, TEAM
    }
}
