package com.atlas_erp.mail_service.config;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

/**
 * Kafka configuration for JSON deserialization of MailEventDTO.
 */
@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.ssl.keystore.location}")
    private String keystoreLocation;

    @Value("${spring.kafka.properties.ssl.truststore.location}")
    private String truststoreLocation;

    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // 1. Dùng ErrorHandlingDeserializer làm Deserializer chính
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);

        // 2. Chỉ định JsonDeserializer là Deserializer thực sự bên trong
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
                "org.springframework.kafka.support.serializer.JsonDeserializer");

        // --- BỔ SUNG CÁC DÒNG DƯỚI ĐÂY ---

        // 3. Chỉ định class mặc định nếu không tìm thấy type trong Header
        // Lưu ý: Kiểm tra chính xác đường dẫn package của MailEventDTO
        props.put("spring.json.value.default.type", "com.atlas_erp.mail_service.dto.MailEventDTO");

        // 4. Tin tưởng package chứa DTO
        props.put("spring.json.trusted.packages", "com.atlas_erp.mail_service.dto");

        // 5. Quan trọng: Bảo Deserializer KHÔNG cần tìm Type thông qua Header
        props.put("spring.json.use.type.headers", false);

        // ---------------------------------

        props.put(ConsumerConfig.GROUP_ID_CONFIG, "mail-service-group");
        props.putAll(sslProperties());

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    private Map<String, Object> sslProperties() {
        Map<String, Object> props = new HashMap<>();
        props.put("security.protocol", "SSL");
        props.put("ssl.keystore.location", keystoreLocation);
        props.put("ssl.keystore.password", "changeit");
        props.put("ssl.keystore.type", "PKCS12");

        props.put("ssl.truststore.location", truststoreLocation);
        props.put("ssl.truststore.password", "changeit");
        props.put("ssl.truststore.type", "PKCS12");
        return props;
    }
}
