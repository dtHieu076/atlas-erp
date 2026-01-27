package com.atlas_erp.organization_service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class OrganizationKafkaConsumer {

    @KafkaListener(topics = "test-topic", groupId = "organization-service-group")
    public void handleMessage(String message) {
        System.out.println(">>> ORGANIZATION SERVICE NHẬN: " + message);
    }
}
