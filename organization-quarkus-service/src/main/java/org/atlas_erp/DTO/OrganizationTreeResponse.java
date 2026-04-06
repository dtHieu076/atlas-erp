package org.atlas_erp.DTO;

import lombok.Data;
import java.util.List;

// API: GET /organizations/tree
@Data
public class OrganizationTreeResponse {

    private List<OrganizationItemDTO> companies;
    private List<PlantItemDTO> branches;
    private List<DepartmentItemDTO> departments;
}
