package com.atlas_erp.hr_service.Dto.Response.Contract;

import lombok.Builder;
import lombok.Data;

import java.time.Instant;
import java.util.List;

@Data
@Builder
public class ContractDetailResponse {

    private String id;
    private String name;
    private Integer defaultDuration;
    private String durationType;
    private String group;
    private String description;
    private Boolean isActive;
    private Instant createdAt;
    private String createdBy;
    private Instant lastUpdated;
    private Long employeeCount;
    private List<String> avatars;
}