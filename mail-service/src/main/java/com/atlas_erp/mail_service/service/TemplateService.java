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
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import com.atlas_erp.mail_service.dto.TemplateDTO;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;

@Service
@RequiredArgsConstructor
@Slf4j
public class TemplateService {

    private final PathMatchingResourcePatternResolver resolver = new PathMatchingResourcePatternResolver();

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

    /**
     * List all templates from /templates/*.html
     */
    public List<TemplateDTO> listTemplates() {
        try {
            Resource[] resources = resolver.getResources("classpath:/templates/*.html");
            return List.of(resources).stream()
                    .map(this::toTemplateDTO)
                    .toList();
        } catch (IOException e) {
            log.error("Error listing templates", e);
            return List.of();
        }
    }

    private TemplateDTO toTemplateDTO(Resource resource) {
        String filename = resource.getFilename();
        String id = filename.replace(".html", "");

        TemplateDTO dto = new TemplateDTO();
        dto.setId(id);
        dto.setName(getTemplateName(id));
        dto.setDescription(getTemplateDesc(id));

        try {
            String content = new String(resource.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
            dto.setRequiredVariables(extractRequiredVariables(content));
        } catch (IOException e) {
            log.error("Error reading template {}", id, e);
        }

        return dto;
    }

    private List<String> extractRequiredVariables(String content) {
        // Sử dụng HashSet để tự động loại bỏ các biến trùng lặp và cho phép .add()
        Set<String> vars = new java.util.HashSet<>();

        // Regex tìm các biến nằm trong ${...}
        Pattern pattern = Pattern.compile("\\$\\{([^}]+)\\}");
        Matcher matcher = pattern.matcher(content);

        while (matcher.find()) {
            String var = matcher.group(1).trim();
            // Chỉ add nếu không bắt đầu bằng dấu # (biến hệ thống Thymeleaf)
            if (!var.startsWith("#")) {
                vars.add(var);
            }
        }

        return vars.stream().toList();
    }

    private String getTemplateName(String id) {
        return switch (id) {
            case "welcome_email" -> "Thư chào mừng nhân viên mới";
            case "holiday_announcement" -> "Thông báo nghỉ lễ";
            case "notification" -> "Thông báo chung";
            default -> id.replace("_", " ").toUpperCase();
        };
    }

    private String getTemplateDesc(String id) {
        return switch (id) {
            case "welcome_email" -> "Dùng khi có nhân sự mới onboarding";
            case "holiday_announcement" -> "Dùng để thông báo lịch nghỉ Tết, lễ";
            case "notification" -> "Template thông báo chung";
            default -> "Template " + id;
        };
    }
}
