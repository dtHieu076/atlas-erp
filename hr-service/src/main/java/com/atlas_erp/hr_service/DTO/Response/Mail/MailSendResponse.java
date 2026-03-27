package com.atlas_erp.hr_service.Dto.Response.Mail;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MailSendResponse {
    private int sentCount;
    private String message;
}
