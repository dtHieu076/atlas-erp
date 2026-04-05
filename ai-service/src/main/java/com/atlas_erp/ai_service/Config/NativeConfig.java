package com.atlas_erp.ai_service.Config;

import com.atlas_erp.ai_service.Dto.Response.ApiResponse;
import com.atlas_erp.ai_service.Dto.Response.ChatResponse;
import com.atlas_erp.ai_service.Entity.AttendanceEntity;
import com.atlas_erp.ai_service.Entity.EmployeeEntity;
import org.springframework.aot.hint.RuntimeHints;
import org.springframework.aot.hint.RuntimeHintsRegistrar;
import org.springframework.aot.hint.annotation.RegisterReflectionForBinding;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportRuntimeHints;

import java.util.UUID;

@Configuration
// 1. Đăng ký Reflection cho các DTO/Entity để Jackson và Spring AI có thể map
// dữ liệu
@RegisterReflectionForBinding({
        ApiResponse.class,
        ChatResponse.class,
        EmployeeEntity.class,
        AttendanceEntity.class
})
// 2. Import các cấu hình AOT can thiệp sâu hơn (như mảng của Postgres hoặc file
// tĩnh)
@ImportRuntimeHints(NativeConfig.AiRuntimeHints.class)
public class NativeConfig {

    public static class AiRuntimeHints implements RuntimeHintsRegistrar {
        @Override
        public void registerHints(RuntimeHints hints, ClassLoader classLoader) {

            // Xử lý lỗi khởi tạo mảng UUID[] của Hibernate/PostgreSQL
            hints.reflection().registerType(UUID[].class);

            // Gợi ý thêm: Đăng ký các file system prompt nếu bạn có lưu trong
            // resources/prompts
            // hints.resources().registerPattern("prompts/*.txt");
            // hints.resources().registerPattern("prompts/*.st");
        }
    }
}