package com.atlas_erp.user_activity_service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.kafka.annotation.EnableKafka;

@SpringBootApplication
@EnableKafka
public class UserActivityServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(UserActivityServiceApplication.class, args);
	}

}
