package com.atlas_erp.admin_service.event;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import lombok.extern.slf4j.Slf4j;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

@Slf4j
@Service
public class BackupKafkaService {

    private static final String COMMANDS_TOPIC = "erp-backup-commands";
    private static final String STATUS_TOPIC = "erp-backup-status";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final Set<String> requiredServices;

    public BackupKafkaService(
            KafkaTemplate<String, String> kafkaTemplate,
            @Value("${backup.required-services:}") String requiredServicesStr) {

        this.kafkaTemplate = kafkaTemplate;

        // Khởi tạo Set các service bắt buộc từ chuỗi config
        if (requiredServicesStr != null && !requiredServicesStr.trim().isEmpty()) {
            this.requiredServices = new HashSet<>(Arrays.asList(requiredServicesStr.split(",")));
        } else {
            this.requiredServices = new HashSet<>();
        }

        log.info("BackupKafkaService initialized with required services: {}", requiredServices);
    }

    /**
     * Send backup command to trigger services
     */
    public void sendBackupCommand() {
        kafkaTemplate.send(COMMANDS_TOPIC, "admin-service-key", "Cần backup");
        log.info("Sent backup command to topic: {}", COMMANDS_TOPIC);
    }

    /**
     * Will be called by status listener to track responses
     */
    public void handleStatusResponse(String serviceName, String message) {
        log.info("Received backup status from {}: {}", serviceName, message);
    }

    /**
     * Kafka listener for backup status responses
     */
    @KafkaListener(topics = "erp-backup-status")
    public void handleBackupStatus(String message) {
        // Expected format: "hr-service:Đã thành công"
        String[] parts = message.split(":", 2);
        if (parts.length == 2) {
            String serviceName = parts[0].trim();
            String status = parts[1].trim();

            // Gọi service trung gian xử lý logic Kafka
            handleStatusResponse(serviceName, status);
            log.info("Received backup status from {}: {}", serviceName, status);

            if (requiredServices.contains(serviceName) && "Đã thành công".equals(status)) {
                log.info("✅ Backup thành công - Required service {} confirmed!", serviceName);
            }
        } else {
            log.warn("Invalid status message format: {}", message);
        }
    }
}
