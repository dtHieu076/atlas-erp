package com.atlas_erp.mail_service.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

@Service
@RequiredArgsConstructor
@Slf4j
public class MailSenderService {

    private final JavaMailSender javaMailSender;
    private static final String FROM_EMAIL = "DangThanhHieu@atlas-erp.com";

    /**
     * Send HTML email asynchronously.
     */
    @Async("mailExecutor") // Chỉ định dùng Thread Pool đã cấu hình ở bước 2
    public CompletableFuture<Boolean> sendHtmlEmailAsync(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, StandardCharsets.UTF_8.name());

            helper.setFrom(FROM_EMAIL);
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // HTML

            javaMailSender.send(mimeMessage);
            log.info("Email sent successfully to {}", to);

            // Trả về true khi gửi thành công
            return CompletableFuture.completedFuture(true);

        } catch (MessagingException | MailException e) {
            log.error("Failed to send email to {}: {}", to, e.getMessage());

            // Trả về false khi gặp lỗi
            return CompletableFuture.completedFuture(false);
        }
    }
}