package com.atlas_erp.hr_service.Cache;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {

    /**
     * Tạo một ObjectMapper chuyên dụng cho Cache.
     * Tắt bỏ hoàn toàn Polymorphic Type (thẻ @class) để tránh lỗi lệch pha
     * Serializer.
     */
    @Bean(name = "cacheObjectMapper")
    @Primary // Ưu tiên dùng bean này khi injection vào RedisCacheService
    public ObjectMapper cacheObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();

        // 1. Hỗ trợ các kiểu dữ liệu Java 8 (LocalDateTime, LocalTime...)
        mapper.registerModule(new JavaTimeModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        // 2. Cấu hình quan trọng: Không ném lỗi khi JSON có trường thừa (đảm bảo tính
        // tương thích)
        mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);

        // 3. ĐẢM BẢO TẮT POLYMORPHIC TYPING:
        // Không sử dụng mapper.activateDefaultTyping(...) ở đây.
        // Điều này giúp JSON lưu trong Redis là JSON thuần khiết (Plain JSON).

        return mapper;
    }

    @Bean
    public RedisTemplate<String, String> redisTemplate(
            RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, String> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // Sử dụng StringSerializer để lưu JSON như một chuỗi văn bản thông thường
        StringRedisSerializer stringSerializer = new StringRedisSerializer();
        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(stringSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(stringSerializer);

        template.afterPropertiesSet();
        return template;
    }
}