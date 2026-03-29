package com.atlas_erp.mail_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Configuration
public class MailConfig {

    // Lấy cấu hình từ file properties hoặc biến môi trường.
    // Nếu không có thì mặc định là localhost và 1025 để chạy dưới máy cá nhân.
    @Value("${spring.mail.host:localhost}")
    private String mailHost;

    @Value("${spring.mail.port:1025}")
    private int mailPort;

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();

        // SỬA Ở ĐÂY: Dùng biến thay vì gán chết chuỗi "localhost"
        mailSender.setHost(mailHost);
        mailSender.setPort(mailPort);

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.transport.protocol", "smtp");
        props.put("mail.smtp.auth", false);
        props.put("mail.smtp.starttls.enable", false);

        return mailSender;
    }
}