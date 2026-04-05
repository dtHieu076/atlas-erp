package com.atlas_erp.mail_service.config;

import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;
import java.util.UUID;

@Configuration
@ImportRuntimeHints(NativeConfig.GraalVMHints.class)
public class NativeConfig {

    public static class GraalVMHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Khai báo cho GraalVM biết Hibernate sẽ dùng mảng UUID[] lúc runtime
            hints.reflection().registerType(UUID[].class);
        }
    }
}