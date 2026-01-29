package com.atlas_erp.organization_service.DTO.Response;

import com.atlas_erp.organization_service.DTO.DepartmentItemDTO;
import lombok.Data;
import java.util.List;

@Data
public class DepartmentListResponse {

    private List<DepartmentItemDTO> list;
    private Long total;
}
