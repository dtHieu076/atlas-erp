package com.atlas_erp.inventory_service.DTO.Response;

import lombok.Data;
import java.util.Date;

@Data
public class CancelStockDocumentResponse {
    private Boolean success;
    private String stockDocumentId;
    private String status;
    private Date cancelledAt;
    private String cancelledBy;
}
