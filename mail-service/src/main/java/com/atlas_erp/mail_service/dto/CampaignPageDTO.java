package com.atlas_erp.mail_service.dto;

import lombok.Data;
import java.util.List;

@Data
public class CampaignPageDTO {
    private List<CampaignResponseDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
