package org.atlas_erp.mail.dto;

import lombok.Data;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import io.quarkus.runtime.annotations.RegisterForReflection;
import java.util.List;
import java.util.Map;

/**
 * DTO for Mail events received from Kafka topic 'mail_requests'.
 * Standardized payload from HR/Marketing services.
 */
@Data
@RegisterForReflection
public class MailEventDTO {

    @NotBlank
    private String templateId;

    @NotNull
    private Map<String, Object> templateData;

    @NotEmpty
    private List<Recipient> recipients;

    private Map<String, Object> tags; // Optional metadata tags

    private String sendType = "INDIVIDUAL"; // "ALL", "TEAM", "INDIVIDUAL"

    // Inner class for recipients
    @Data
    @RegisterForReflection
    public static class Recipient {
        @NotBlank
        private String email;
        private String name;
    }
}
