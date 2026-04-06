package org.atlas_erp.mail.service;

import org.atlas_erp.mail.dto.TemplateDTO;
import org.atlas_erp.mail.model.MailJob;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import io.quarkus.qute.Engine;
import java.util.*;
import java.util.logging.Logger;

/**
 * Template service using Qute (Quarkus template engine).
 */
@ApplicationScoped
public class TemplateService {

    private static final Logger log = Logger.getLogger(TemplateService.class.getName());

    @Inject
    Engine engine;

    private final ObjectMapper objectMapper = new ObjectMapper();

    /**
     * Render HTML from templateId and templateData JSON.
     */
    public String render(String templateId, String templateDataJson) {
        try {
            Map<String, Object> data = objectMapper.readValue(
                    templateDataJson,
                    new TypeReference<Map<String, Object>>() {
                    });

            // Load template from templates folder
            Template template = engine.getTemplate(templateId);
            if (template != null) {
                TemplateInstance instance = template.instance();
                data.forEach(instance::data);
                return instance.render();
            } else {
                log.warning("Template " + templateId + " not found");
                return "";
            }
        } catch (Exception e) {
            log.severe("Error rendering template " + templateId + ": " + e.getMessage());
            return "";
        }
    }

    public String render(MailJob job) {
        try {
            String templateDataJson = objectMapper.writeValueAsString(job.getTemplateData());
            return render(job.getTemplateId(), templateDataJson);
        } catch (Exception e) {
            log.severe("Error converting JsonNode to String: " + e.getMessage());
            return render(job.getTemplateId(), "{}");
        }
    }

    /**
     * List all available templates.
     */
    public List<TemplateDTO> listTemplates() {
        List<TemplateDTO> templates = new ArrayList<>();
        // In Quarkus Qute, we need to register templates statically
        String[] templateNames = { "welcome_email", "holiday", "notification" };

        for (String name : templateNames) {
            try {
                Template template = engine.getTemplate(name);
                if (template != null) {
                    TemplateDTO dto = new TemplateDTO();
                    dto.setId(name);
                    dto.setName(getTemplateName(name));
                    dto.setDescription(getTemplateDesc(name));
                    // Extract variables from template content
                    dto.setRequiredVariables(List.of());
                    templates.add(dto);
                }
            } catch (Exception e) {
                log.severe("Template " + name + " not found: " + e.getMessage());
            }
        }

        return templates;
    }

    private String getTemplateName(String id) {
        return switch (id) {
            case "welcome_email" -> "Thư chào mừng nhân viên mới";
            case "holiday" -> "Thông báo nghỉ lễ";
            case "notification" -> "Thông báo chung";
            default -> id.replace("_", " ").toUpperCase();
        };
    }

    private String getTemplateDesc(String id) {
        return switch (id) {
            case "welcome_email" -> "Dùng khi có nhân sự mới onboarding";
            case "holiday" -> "Dùng để thông báo lịch nghỉ Tết, lễ";
            case "notification" -> "Template thông báo chung";
            default -> "Template " + id;
        };
    }
}
