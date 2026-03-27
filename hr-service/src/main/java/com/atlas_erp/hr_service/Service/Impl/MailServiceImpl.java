package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Dto.MailRecipientDTO;
import com.atlas_erp.hr_service.Dto.Request.Mail.MailSendRequest;
import com.atlas_erp.hr_service.Dto.Response.Mail.MailSendResponse;
import com.atlas_erp.hr_service.Entity.EmployeeEntity;
import com.atlas_erp.hr_service.Event.dtoEvent.MailRequestEvent;
import com.atlas_erp.hr_service.Repository.EmployeeRepository;
import com.atlas_erp.hr_service.Service.MailService;
import com.atlas_erp.hr_service.enums.SendType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import com.atlas_erp.hr_service.Dto.Request.Mail.MailSendRequest; // For recipients access

@Service
@Slf4j
@RequiredArgsConstructor
@Transactional
public class MailServiceImpl implements MailService {

        private final EmployeeRepository employeeRepository;

        // Cập nhật lại kiểu dữ liệu thành <String, String> để hợp với StringSerializer
        // của bạn
        private final KafkaTemplate<String, String> kafkaTemplate;

        // Inject thêm ObjectMapper để chuyển Object thành JSON
        private final ObjectMapper objectMapper;

        @Override
        public MailSendResponse sendMail(MailSendRequest request) {
                log.info("Processing mail send request: templateId={}, sendType={}",
                                request.getTemplateId(), request.getSendType());

                // Get recipients based on sendType
                List<EmployeeEntity> employees = getRecipients(request);
                List<MailRecipientDTO> recipients = employees.stream()
                                .map(this::mapToRecipient)
                                .collect(Collectors.toList());

                if (recipients.isEmpty()) {
                        return MailSendResponse.builder()
                                        .sentCount(0)
                                        .message("No active recipients found for the specified targets")
                                        .build();
                }

                // Build Kafka event - Safe conversion Map<String,String> to Map<String,Object>
                Map<String, Object> finalTemplateData = new HashMap<>();
                if (request.getTemplateData() != null) {
                        finalTemplateData.putAll(request.getTemplateData());
                }

                MailRequestEvent event = MailRequestEvent.builder()
                                .templateId(request.getTemplateId())
                                .templateData(finalTemplateData)
                                .recipients(recipients)
                                .tags(request.getTags() != null ? request.getTags() : new ArrayList<>())
                                .sendType(request.getSendType().name())
                                .build();

                try {
                        // Chuyển event (Object) thành json (String)
                        String eventJsonString = objectMapper.writeValueAsString(event);

                        // Send chuỗi JSON to Kafka thay vì gửi Object
                        kafkaTemplate.send("mail_requests", eventJsonString);
                        log.info("Published MailRequestEvent to kafka with {} recipients", recipients.size());

                } catch (JsonProcessingException e) {
                        log.error("Lỗi khi convert MailRequestEvent sang JSON: ", e);
                        throw new RuntimeException("Error processing mail request payload", e);
                }

                return MailSendResponse.builder()
                                .sentCount(recipients.size())
                                .message(String.format("Successfully queued %d emails for sending", recipients.size()))
                                .build();
        }

        private List<EmployeeEntity> getRecipients(MailSendRequest request) {
                SendType sendType = request.getSendType();
                List<UUID> targetIds = request.getTargetIds();
                List<MailRecipientDTO> recipientsInput = request.getRecipients();

                return switch (sendType) {
                        case ALL -> employeeRepository.findActiveEmployees();
                        case TEAM -> targetIds != null && !targetIds.isEmpty()
                                        ? employeeRepository.findActiveByTeams(targetIds)
                                        : List.of();
                        case INDIVIDUAL -> {
                                if (recipientsInput != null && !recipientsInput.isEmpty()) {
                                        List<String> emails = recipientsInput.stream()
                                                        .map(MailRecipientDTO::getEmail)
                                                        .filter(email -> email != null && !email.trim().isEmpty())
                                                        .toList();
                                        if (!emails.isEmpty()) {
                                                yield employeeRepository.findActiveByWorkEmails(emails);
                                        }
                                }
                                if (targetIds != null && !targetIds.isEmpty()) {
                                        yield employeeRepository.findActiveByIds(targetIds);
                                }
                                yield List.of();
                        }
                };
        }

        private MailRecipientDTO mapToRecipient(EmployeeEntity employee) {
                return MailRecipientDTO.builder()
                                .email(employee.getWorkEmail())
                                .name(employee.getFullName())
                                .build();
        }
}