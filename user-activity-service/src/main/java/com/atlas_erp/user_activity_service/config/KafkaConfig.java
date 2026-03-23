package com.atlas_erp.user_activity_service.config;

import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.*;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${spring.kafka.properties.ssl.keystore.location:}")
    private String keystoreLocation;

    @Value("${spring.kafka.properties.ssl.truststore.location:}")
    private String truststoreLocation;

    // 1. Tạo Topic trên Cloud (nếu Cloud cho phép tự động tạo qua AdminClient)
    @Bean
    public NewTopic activityLogTopic() {
        return TopicBuilder.name("user-activity-log")
                .partitions(3)
                .replicas(3) // Cloud thường yêu cầu tối thiểu 3 replicas để đảm bảo an toàn
                .build();
    }

    // 2. Cấu hình Producer để gửi log lên Cloud
    @Bean
    public ProducerFactory<String, Object> producerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        // Sử dụng String class name để tránh cảnh báo Deprecated trong Spring 4.x
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG,
                "org.springframework.kafka.support.serializer.JsonSerializer");

        // Cấu hình JSON
        props.put("spring.json.add.type.headers", false);

        // Nạp cấu hình SSL/Security cho Cloud
        props.putAll(sslProperties());

        return new DefaultKafkaProducerFactory<>(props);
    }

    @Bean
    public KafkaTemplate<String, Object> kafkaTemplate() {
        return new KafkaTemplate<>(producerFactory());
    }

    // 3. Cấu hình Consumer để nhận log (Sử dụng ErrorHandlingDeserializer giống
    // mail-service)
    @Bean
    public ConsumerFactory<String, Object> consumerFactory() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.GROUP_ID_CONFIG, "user-activity-group");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);

        // Giống mail-service: Chống "tin nhắn độc" làm sập hệ thống
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, ErrorHandlingDeserializer.class);
        props.put(ErrorHandlingDeserializer.VALUE_DESERIALIZER_CLASS,
                "org.springframework.kafka.support.serializer.JsonDeserializer");

        // Cấu hình cụ thể cho UserActivity (Điều chỉnh đúng package của bạn)
        props.put("spring.json.value.default.type", "com.atlas_erp.user_activity_service.dto.ActivityLogRequest");
        props.put("spring.json.trusted.packages", "com.atlas_erp.user_activity_service.*");
        props.put("spring.json.use.type.headers", false);

        props.putAll(sslProperties());

        return new DefaultKafkaConsumerFactory<>(props);
    }

    @Bean
    public ConcurrentKafkaListenerContainerFactory<String, Object> kafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, Object> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(consumerFactory());
        return factory;
    }

    // 4. Hàm bổ trợ nạp cấu hình bảo mật (SSL)
    private Map<String, Object> sslProperties() {
        Map<String, Object> props = new HashMap<>();
        // Nếu dùng Confluent Cloud hoặc các bên tương tự thường dùng SASL_SSL
        // Ở đây tôi giữ nguyên chuẩn SSL giống mail-service bạn cung cấp
        props.put("security.protocol", "SSL");
        props.put("ssl.keystore.location", keystoreLocation);
        props.put("ssl.keystore.password", "changeit"); // Thay bằng pass thật nếu cần
        props.put("ssl.keystore.type", "PKCS12");

        props.put("ssl.truststore.location", truststoreLocation);
        props.put("ssl.truststore.password", "changeit");
        props.put("ssl.truststore.type", "PKCS12");

        // Fix lỗi host name verification trên một số Cloud provider
        props.put("ssl.endpoint.identification.algorithm", "https");

        return props;
    }
}