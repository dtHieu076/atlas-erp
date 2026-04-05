package com.atlas_erp.ai_service.Config;

import org.springframework.aot.hint.MemberCategory;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.TypeReference;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

@Configuration
@ImportRuntimeHints(GraalVMConfig.NettyHints.class)
public class GraalVMConfig {

    // Lớp này dùng để "nhắc nhở" GraalVM giữ lại các biến ngầm của Netty
    static class NettyHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // Dặn GraalVM giữ lại các biến (Fields) trong các class JCTools của Netty
            hints.reflection().registerType(
                    TypeReference.of(
                            "io.netty.util.internal.shaded.org.jctools.queues.unpadded.MpscUnpaddedArrayQueueProducerIndexField"),
                    MemberCategory.DECLARED_FIELDS);
            hints.reflection().registerType(
                    TypeReference.of(
                            "io.netty.util.internal.shaded.org.jctools.queues.unpadded.MpscUnpaddedArrayQueueConsumerIndexField"),
                    MemberCategory.DECLARED_FIELDS);
        }
    }
}