package com.atlas_erp.mail_service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync // Bật tính năng chạy bất đồng bộ cho Spring
public class AsyncConfig {

    @Bean(name = "mailExecutor")
    public Executor mailExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(10); // Luôn duy trì 10 luồng làm việc đồng thời
        executor.setMaxPoolSize(50); // Khi quá tải, có thể mở rộng lên tối đa 50 luồng
        executor.setQueueCapacity(500); // Hàng đợi chứa 500 email đang chờ được gửi
        executor.setThreadNamePrefix("MailAsync-"); // Đặt tên để dễ debug trong log
        executor.initialize();
        return executor;
    }
}