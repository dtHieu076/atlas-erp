package com.atlas_erp.hr_service.Service;

import com.atlas_erp.hr_service.Dto.Request.Mail.MailSendRequest;
import com.atlas_erp.hr_service.Dto.Response.Mail.MailSendResponse;

public interface MailService {
    MailSendResponse sendMail(MailSendRequest request);
}
