package com.atlas_erp.production_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "product")
@Data
public class ProductEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String code;
    private String name;
    private String category;
    private String unit;
    private String status;
}
