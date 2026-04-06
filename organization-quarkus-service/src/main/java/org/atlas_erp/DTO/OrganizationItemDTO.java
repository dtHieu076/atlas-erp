package org.atlas_erp.DTO;

import lombok.Data;
import java.util.UUID;

@Data
public class OrganizationItemDTO {

    private UUID id;
    private String code;
    private String name;
    private String taxCode;
    private String address;
    private String status;
}
