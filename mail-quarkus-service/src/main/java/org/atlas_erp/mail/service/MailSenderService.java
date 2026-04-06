package org.atlas_erp.mail.service;

import io.quarkus.mailer.Mail;
import io.quarkus.mailer.reactive.ReactiveMailer; // Đổi sang import bản Reactive
import io.smallrye.mutiny.Uni;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import java.util.logging.Logger;

@ApplicationScoped
public class MailSenderService {

    private static final Logger log = Logger.getLogger(MailSenderService.class.getName());
    private static final String FROM_EMAIL = "DangThanhHieu@atlas-erp.com";

    @Inject
    ReactiveMailer reactiveMailer; // Inject ReactiveMailer

    /**
     * Send HTML email asynchronously using Quarkus Mutiny.
     */
    public Uni<Boolean> sendHtmlEmailAsync(String to, String subject, String htmlContent) {
        Mail mail = Mail.withHtml(to, subject, htmlContent)
                .setFrom(FROM_EMAIL);

        // Gửi mail và xử lý kết quả bằng Mutiny Uni
        return reactiveMailer.send(mail)
                .onItem().invoke(() -> log.info("Email sent successfully to " + to))
                // Vì .send() trả về Uni<Void>, ta dùng replaceWith để đổi kết quả trả về thành
                // true
                .replaceWith(true)
                .onFailure().invoke(e -> log.severe("Failed to send email to " + to + ": " + e.getMessage()))
                // Nếu lỗi thì dập lỗi và trả về false
                .onFailure().recoverWithItem(false);
    }
}