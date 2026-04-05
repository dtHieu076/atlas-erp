package com.atlas_erp.mail_service.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;

public class TemplateHints implements RuntimeHintsRegistrar {
    @Override
    public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
        // Báo cho GraalVM gom tất cả file .html trong thư mục templates vào Native
        // Image
        hints.resources().registerPattern("templates/*.html");
    }
}
