package com.atlas_erp.hr_service.Controller;

import com.atlas_erp.hr_service.Dto.Request.Mail.MailSendRequest;
import com.atlas_erp.hr_service.Dto.Response.ApiResponse;
import com.atlas_erp.hr_service.Dto.Response.Mail.MailSendResponse;
import com.atlas_erp.hr_service.Service.MailService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/mail")
@RequiredArgsConstructor
public class MailController {

    private final MailService mailService;

    @PostMapping("/send")
    public ApiResponse<MailSendResponse> sendMail(@RequestBody MailSendRequest request) {
        MailSendResponse response = mailService.sendMail(request);
        return ApiResponse.success("Mail queued successfully", response);
    }
}
