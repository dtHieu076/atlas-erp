package com.atlas_erp.admin_service.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true) // Cứu tinh giúp bỏ qua các trường lạ (như csiSnapshotTimeout)
public class VeleroBackupSpec {
    private List<String> includedNamespaces;
    private String ttl;
}