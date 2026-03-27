package com.atlas_erp.mail_service.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import java.util.List;
import java.util.Map;

/**
 * DTO for Mail events received from Kafka topic 'mail_requests'.
 * Standardized payload from HR/Marketing services.
 */
@Data
public class MailEventDTO {

    @NotBlank
    private String templateId;

    @NotNull
    private Map<String, Object> templateData;

    @NotEmpty
    private List<Recipient> recipients;

    private List<String> tags;

    private String sendType = "INDIVIDUAL"; // "ALL", "TEAM", "INDIVIDUAL"

    // Inner class for recipients
    @Data
    public static class Recipient {
        @NotBlank
        private String email;
        private String name;
    }
}
