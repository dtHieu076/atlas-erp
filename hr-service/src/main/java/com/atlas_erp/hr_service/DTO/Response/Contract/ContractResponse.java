package com.atlas_erp.hr_service.Dto.Response.Contract;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ContractResponse {

    private String id;
    private String code;
    private String name;
    private String duration;
    private String policy;
    private String status;
}
