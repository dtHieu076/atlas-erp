package com.atlas_erp.accounting_service.Event.serviceEvent;

import com.atlas_erp.accounting_service.Event.dtoEvent.AccountingPeriodLockedEvent;
import com.atlas_erp.accounting_service.Event.dtoEvent.JournalEntryPostedEvent;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountingEventService {

    private static final String TOPIC_PERIOD_LOCKED = "accounting.period.locked";

    private final KafkaTemplate<String, String> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public void publishPeriodLocked(AccountingPeriodLockedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_PERIOD_LOCKED, event.getPeriodId(), payload);
            log.info("🚀 Đã phát event khóa sổ kế toán: {}", payload);
        } catch (Exception e) {
            log.error("❌ Lỗi khi phát Kafka event khóa sổ: {}", e.getMessage());
            throw new RuntimeException("Serialize / send Kafka failed", e);
        }
    }

    private static final String TOPIC_JOURNAL_POSTED = "accounting.journal.posted";

    public void publishJournalPosted(JournalEntryPostedEvent event) {
        try {
            String payload = objectMapper.writeValueAsString(event);
            kafkaTemplate.send(TOPIC_JOURNAL_POSTED, event.getEntryId().toString(), payload);
            log.info("🚀 Đã phát event ghi sổ bút toán: {}", payload);
        } catch (Exception e) {
            log.error("❌ Lỗi khi phát Kafka event Journal Posted: {}", e.getMessage());
        }
    }
}