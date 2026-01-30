package com.atlas_erp.accounting_service.Entity;

import jakarta.persistence.*;
import lombok.*;
import java.util.UUID;

@Entity
@Table(name = "account")
@Data
public class AccountEntity {

    @Id
    @GeneratedValue
    private UUID id;

    private String accountCode;
    private String accountName;
    private String accountType;
}
