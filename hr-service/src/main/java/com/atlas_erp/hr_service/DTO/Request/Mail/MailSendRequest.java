package com.atlas_erp.hr_service.Dto.Request.Mail;

import com.atlas_erp.hr_service.Dto.MailRecipientDTO;
import com.atlas_erp.hr_service.enums.SendType;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailSendRequest {

    private String templateId;

    private SendType sendType;

    private List<UUID> targetIds;

    private List<MailRecipientDTO> recipients;

    private Map<String, String> templateData;

    private List<String> tags;
}
