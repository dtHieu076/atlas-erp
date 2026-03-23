package com.atlas_erp.mail_service.service;

import com.atlas_erp.mail_service.model.MailJob;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {

    private final SpringTemplateEngine templateEngine;

    // Khởi tạo trực tiếp thay vì inject để tránh lỗi "No qualifying bean"
    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Render HTML từ templateId (tên file .html) và templateData JSON.
     */
    public String render(String templateId, String templateDataJson) {
        Context context = new Context();

        try {
            // Chuyển đổi JSON string sang Map một cách an toàn
            Map<String, Object> data = objectMapper.readValue(
                    templateDataJson,
                    new TypeReference<Map<String, Object>>() {
                    });
            context.setVariables(data);
        } catch (Exception e) {
            log.error("Lỗi parse JSON cho template {}: {}", templateId, e.getMessage());
        }

        return templateEngine.process(templateId, context);
    }

    public String render(MailJob job) {
        return render(job.getTemplateId(), job.getTemplateData());
    }
}