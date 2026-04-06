package org.atlas_erp.DTO;

import lombok.Data;
import java.util.UUID;

@Data
public class DepartmentItemDTO {

    private UUID id;
    private String code;
    private String name;
    private UUID plantId;
    private String status;
}
