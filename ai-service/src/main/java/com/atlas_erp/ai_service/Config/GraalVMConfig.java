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

    static class NettyHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {
            // 1. Giữ lại producerIndex
            hints.reflection().registerType(
                    TypeReference.of(
                            "io.netty.util.internal.shaded.org.jctools.queues.unpadded.MpscUnpaddedArrayQueueProducerIndexField"),
                    MemberCategory.DECLARED_FIELDS);
            // 2. Giữ lại consumerIndex
            hints.reflection().registerType(
                    TypeReference.of(
                            "io.netty.util.internal.shaded.org.jctools.queues.unpadded.MpscUnpaddedArrayQueueConsumerIndexField"),
                    MemberCategory.DECLARED_FIELDS);
            // 3. Giữ lại producerLimit (Bổ sung thêm dòng này để trị dứt điểm lỗi hiện tại)
            hints.reflection().registerType(
                    TypeReference.of(
                            "io.netty.util.internal.shaded.org.jctools.queues.unpadded.MpscUnpaddedArrayQueueProducerLimitField"),
                    MemberCategory.DECLARED_FIELDS);
        }
    }
}