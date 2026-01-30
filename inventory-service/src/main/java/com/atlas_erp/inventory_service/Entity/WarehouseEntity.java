package com.atlas_erp.inventory_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "warehouse")
@Data
public class WarehouseEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private UUID plantId;

    private String code;
    private String name;
    private String location;
}
