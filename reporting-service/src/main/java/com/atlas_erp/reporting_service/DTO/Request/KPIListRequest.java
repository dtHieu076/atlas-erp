package com.atlas_erp.reporting_service.Dto.Request;

import lombok.Data;

@Data
public class KPIListRequest {

    private String kpiType;
    private String periodType;
    private String periodValue;
    private String departmentId;
}
