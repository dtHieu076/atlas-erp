package com.atlas_erp.hr_service.Event.serviceEvent;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Value;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class BackupEventService {

    private static final String COMMANDS_TOPIC = "erp-backup-commands";
    private static final String STATUS_TOPIC = "erp-backup-status";

    private final KafkaTemplate<String, Object> kafkaTemplate;

    // Lấy thông tin từ properties (đã cấu hình ở bước trước)
    @Value("${spring.datasource.url}")
    private String dbUrl;

    @Value("${spring.datasource.username}")
    private String dbUser;

    @Value("${spring.datasource.password}")
    private String dbPassword;

    @KafkaListener(topics = COMMANDS_TOPIC)
    public void handleBackupCommand(String message) {
        log.info("HR Service received backup command: {}", message);

        // New format: "jobId:Cần backup"
        String[] parts = message.split(":", 2);
        if (parts.length == 2) {
            try {
                UUID jobId = UUID.fromString(parts[0]);
                String command = parts[1];

                if ("Cần backup".equals(command)) {
                    try {
                        // 1. Thực hiện dump DB
                        String filePath = executeDump();
                        log.info("Job {} - Database dumped successfully to: {}", jobId, filePath);

                        // 2. Gửi status với Job ID: "jobId:hr-service:Đã thành công"
                        String statusMessage = jobId.toString() + ":hr-service:Đã thành công";
                        kafkaTemplate.send(STATUS_TOPIC, "hr-service-job-" + jobId, statusMessage);
                        log.info("Job {} - HR Service sent success status to Kafka", jobId);

                    } catch (Exception e) {
                        log.error("Job {} - Failed to dump database: {}", jobId, e.getMessage());
                        // Gửi lỗi với Job ID
                        String errorMessage = jobId.toString() + ":hr-service:Lỗi - " + e.getMessage();
                        kafkaTemplate.send(STATUS_TOPIC, "hr-service-job-" + jobId, errorMessage);
                    }
                }
            } catch (IllegalArgumentException e) {
                log.warn("Invalid Job ID in command, ignoring: {}", message);
                // Fallback to old logic for compatibility
                handleLegacyCommand(message);
            }
        } else {
            // Fallback for old format
            handleLegacyCommand(message);
        }
    }

    private void handleLegacyCommand(String message) {
        if ("Cần backup".equals(message)) {
            try {
                String filePath = executeDump();
                log.info("Legacy - Database dumped to: {}", filePath);
                String statusMessage = "hr-service:Đã thành công";
                kafkaTemplate.send(STATUS_TOPIC, "hr-service-key", statusMessage);
            } catch (Exception e) {
                log.error("Legacy backup failed: {}", e.getMessage());
            }
        }
    }

    private String executeDump() throws IOException, InterruptedException {
        // Tạo tên file theo thời gian thực
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "hr_backup_" + timestamp + ".sql";

        String backupDir = "/app/backups";
        File directory = new File(backupDir);
        if (!directory.exists())
            directory.mkdirs();

        String fullPath = backupDir + "/" + fileName;

        // Parse JDBC URL
        String cleanUrl = dbUrl.replace("jdbc:postgresql://", "");
        String hostAndPort = cleanUrl.split("/")[0];
        String dbName = cleanUrl.split("/")[1].split("\\?")[0];

        log.info("Starting pg_dump for database: {}", dbName);

        ProcessBuilder pb = new ProcessBuilder(
                "pg_dump",
                "-h", hostAndPort.split(":")[0],
                "-p", hostAndPort.contains(":") ? hostAndPort.split(":")[1] : "5432",
                "-U", dbUser,
                "-f", fullPath,
                dbName);

        pb.environment().put("PGPASSWORD", dbPassword);
        pb.redirectErrorStream(true);

        Process process = pb.start();
        int exitCode = process.waitFor();

        if (exitCode != 0) {
            throw new RuntimeException("pg_dump process exited with code " + exitCode);
        }

        return fullPath;
    }
}
