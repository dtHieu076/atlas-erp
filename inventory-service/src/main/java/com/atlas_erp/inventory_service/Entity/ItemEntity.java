package com.atlas_erp.inventory_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "item")
@Data
public class ItemEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private String name;
    private String itemType;
    private String unit;
}
