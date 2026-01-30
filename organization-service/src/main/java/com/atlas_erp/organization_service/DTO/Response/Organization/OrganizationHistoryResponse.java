package com.atlas_erp.organization_service.Dto.Response.Organization;

import lombok.Data;
import java.util.List;

import com.atlas_erp.organization_service.Dto.OrganizationHistoryItemDTO;

@Data
public class OrganizationHistoryResponse {

    private List<OrganizationHistoryItemDTO> historyList;
}
