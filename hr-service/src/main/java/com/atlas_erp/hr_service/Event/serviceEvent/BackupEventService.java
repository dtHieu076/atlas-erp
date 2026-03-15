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

        if ("Cần backup".equals(message)) {
            try {
                // 1. Thực hiện dump DB
                String filePath = executeDump();
                log.info("Database dumped successfully to: {}", filePath);

                // 2. Chỉ khi dump thành công mới gửi tin nhắn xác nhận
                String statusMessage = "hr-service:Đã thành công";
                kafkaTemplate.send(STATUS_TOPIC, "hr-service-key", statusMessage);
                log.info("HR Service sent success status to Kafka");

            } catch (Exception e) {
                log.error("Failed to dump database: {}", e.getMessage());
                // Gửi thông báo lỗi (tùy chọn)
                kafkaTemplate.send(STATUS_TOPIC, "hr-service:Lỗi - " + e.getMessage());
            }
        }
    }

    private String executeDump() throws IOException, InterruptedException {
        // Tạo tên file theo thời gian thực
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        String fileName = "hr_backup_" + timestamp + ".sql";

        // Thư mục lưu trữ local (Sửa lại đường dẫn phù hợp với máy bạn hoặc K8s mount
        // path)
        // String backupDir =
        // "C:\\Users\\Public\\DAIHOC\\wood-erp-platform\\hr-service\\src\\main\\java\\com\\atlas_erp\\hr_service\\Event";
        String backupDir = "/app/backups";
        File directory = new File(backupDir);
        if (!directory.exists())
            directory.mkdirs();

        String fullPath = backupDir + "/" + fileName;

        // Phân tích JDBC URL để lấy Host, Port, DB Name
        // jdbc:postgresql://<host>:<port>/<db_name>
        String cleanUrl = dbUrl.replace("jdbc:postgresql://", "");
        String hostAndPort = cleanUrl.split("/")[0];
        String dbName = cleanUrl.split("/")[1].split("\\?")[0];

        log.info("Starting pg_dump for database: {}", dbName);

        // Xây dựng lệnh pg_dump
        ProcessBuilder pb = new ProcessBuilder(
                "pg_dump",
                "-h", hostAndPort.split(":")[0],
                "-p", hostAndPort.contains(":") ? hostAndPort.split(":")[1] : "5432",
                "-U", dbUser,
                "-f", fullPath,
                dbName);

        // Truyền mật khẩu qua biến môi trường (PGPASSWORD)
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