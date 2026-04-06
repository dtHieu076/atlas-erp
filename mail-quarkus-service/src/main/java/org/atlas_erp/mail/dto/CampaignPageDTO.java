package org.atlas_erp.mail.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CampaignPageDTO {
    private List<CampaignResponseDTO> content;
    private int page;
    private int size;
    private long totalElements;
    private int totalPages;
}
