package com.atlas_erp.inventory_service.Event.serviceEvent;

import com.atlas_erp.inventory_service.Event.dtoEvent.StockDocumentCancelledEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class StockDocumentEventService {

    // private final KafkaTemplate<String, Object> kafkaTemplate; // Sẽ dùng khi có
    // Kafka

    /**
     * Phát sự kiện khi chứng từ bị hủy
     */
    public void publishCancelledEvent(StockDocumentCancelledEvent event) {
        log.info("📣 Đang phát sự kiện HỦY CHỨNG TỪ [{}]: ID={}, Lý do={}",
                event.getDocumentType(), event.getStockDocumentId(), event.getReason());

        try {
            // 1. Logic gửi tới Message Broker (Kafka/RabbitMQ)
            // kafkaTemplate.send(StockDocumentCancelledEvent.TOPIC,
            // event.getStockDocumentId().toString(), event);

            // 2. Logic gọi tới Workflow Service hoặc Accounting Service (nếu dùng
            // Feign/Sync)
            log.info("✅ Sự kiện đã được gửi tới Message Broker thành công.");

        } catch (Exception e) {
            log.error("❌ Lỗi khi phát sự kiện: {}", e.getMessage());
            // Có thể bổ sung logic lưu vào bảng Outbox để gửi lại sau (Transactional Outbox
            // Pattern)
        }
    }
}