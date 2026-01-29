package com.atlas_erp.organization_service.DTO.Response;

import com.atlas_erp.organization_service.DTO.OrganizationHistoryItemDTO;
import lombok.Data;
import java.util.List;

@Data
public class OrganizationHistoryResponse {

    private List<OrganizationHistoryItemDTO> historyList;
}
