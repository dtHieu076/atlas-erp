package com.atlas_erp.iam_service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.kafka.core.KafkaTemplate;

import jakarta.annotation.PostConstruct;

@SpringBootApplication
public class IamServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(IamServiceApplication.class, args);
	}

	@Bean
	public CommandLineRunner testRedis(StringRedisTemplate redisTemplate) {
		return args -> {
			try {
				redisTemplate.opsForValue().set("test_connection", "OK");
				String value = redisTemplate.opsForValue().get("test_connection");
				System.out.println(">>> KẾT NỐI REDIS THÀNH CÔNG: " + value);
			} catch (Exception e) {
				System.err.println(">>> LỖI KẾT NỐI REDIS: " + e.getMessage());
			}
		};
	}

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@PostConstruct
	public void testKafka() {
		kafkaTemplate.send("test-topic", "hello kafka ssl");
	}

}
