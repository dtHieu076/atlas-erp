package com.atlas_erp.hr_service.Event.dtoEvent;

import com.atlas_erp.hr_service.Dto.MailRecipientDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailRequestEvent {
    private String templateId;
    private Map<String, Object> templateData;
    private List<MailRecipientDTO> recipients;
    private List<String> tags;
    private String sendType = "INDIVIDUAL";
}
