package com.atlas_erp.production_service.Service.Impl;

import com.atlas_erp.production_service.Dto.Request.ProductionExecution.*;
import com.atlas_erp.production_service.Dto.Response.ProductionExecution.*;
import com.atlas_erp.production_service.Entity.ProductionExecutionEntity;
import com.atlas_erp.production_service.Entity.ProductionOrderEntity;
import com.atlas_erp.production_service.Event.dtoEvent.ProductionExecutionFinishedEvent;
import com.atlas_erp.production_service.Event.dtoEvent.ProductionExecutionPausedEvent;
import com.atlas_erp.production_service.Event.dtoEvent.ProductionExecutionResumedEvent;
import com.atlas_erp.production_service.Event.dtoEvent.ProductionExecutionStartedEvent;
import com.atlas_erp.production_service.Event.serviceEvent.ProductionEventService;
import com.atlas_erp.production_service.Repository.ProductionExecutionRepository;
import com.atlas_erp.production_service.Repository.ProductionOrderRepository;
import com.atlas_erp.production_service.Service.ProductionExecutionService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import org.springframework.stereotype.Service;
import java.util.UUID;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.Date;

@Service
@RequiredArgsConstructor
@Transactional
@Slf4j
public class ProductionExecutionServiceImpl implements ProductionExecutionService {

    private final ProductionOrderRepository orderRepository;
    private final ProductionExecutionRepository executionRepository;
    private final ProductionEventService eventService;

    @Override
    @Transactional
    public StartExecutionResponse startExecution(StartExecutionRequest request) {
        log.info("Starting execution for Order ID: {}", request.getOrderId());

        // 1. Fetch và Validate Lệnh sản xuất (Production Order)
        ProductionOrderEntity order = orderRepository.findById(UUID.fromString(request.getOrderId()))
                .orElseThrow(() -> new RuntimeException("Production Order not found"));

        // Chỉ cho phép bắt đầu nếu lệnh đang ở trạng thái CREATED
        if (!"CREATED".equalsIgnoreCase(order.getStatus()) && !"IN_PROGRESS".equalsIgnoreCase(order.getStatus())) {
            throw new IllegalStateException("Lệnh sản xuất không ở trạng thái hợp lệ để bắt đầu.");
        }

        // 2. Tạo mới thực thi sản xuất (Production Execution)
        ProductionExecutionEntity execution = new ProductionExecutionEntity();
        execution.setProductionOrderId(order.getId());

        // Giả định Stage ID được lấy từ Order hoặc quy trình mặc định (Routing)
        // Ở đây tạm lấy từ logic nghiệp vụ của bạn hoặc bổ sung vào Request nếu cần
        // execution.setProductionStageId(...);

        execution.setTeamId(UUID.fromString(request.getTeamId()));
        execution.setEmployeeId(UUID.fromString(request.getStartedBy()));

        Instant startTime = (request.getStartTime() != null)
                ? request.getStartTime().toInstant()
                : Instant.now();
        execution.setStartTime(startTime);

        // Khởi tạo số lượng ban đầu
        execution.setGoodQuantity(BigDecimal.ZERO);
        execution.setDefectQuantity(BigDecimal.ZERO);

        // Lưu vào Database
        execution = executionRepository.save(execution);

        // 3. Cập nhật trạng thái Lệnh sản xuất sang IN_PROGRESS
        if ("CREATED".equalsIgnoreCase(order.getStatus())) {
            order.setStatus("IN_PROGRESS");
            orderRepository.save(order);
        }

        // 4. Phát sự kiện Kafka
        ProductionExecutionStartedEvent event = ProductionExecutionStartedEvent.builder()
                .executionId(execution.getId())
                .orderId(order.getId())
                .teamId(request.getTeamId())
                .startTime(startTime)
                .build();

        eventService.publishExecutionStarted(event, execution.getId());

        // 5. Build Response
        StartExecutionResponse response = new StartExecutionResponse();
        response.setExecutionId(execution.getId().toString());

        log.info("Execution started successfully with ID: {}", execution.getId());
        return response;
    }

    @Override
    @Transactional
    public FinishExecutionResponse finishExecution(FinishExecutionRequest request) {
        log.info("Kết thúc thực thi sản xuất ID: {}", request.getExecutionId());

        // 1. Tìm bản ghi thực thi (Execution)
        ProductionExecutionEntity execution = executionRepository.findById(UUID.fromString(request.getExecutionId()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu thực thi sản xuất."));

        // Kiểm tra nếu đã kết thúc rồi thì không cho phép kết thúc lần nữa
        if (execution.getEndTime() != null) {
            throw new IllegalStateException("Thực thi này đã kết thúc trước đó.");
        }

        // 2. Tìm Lệnh sản xuất tương ứng (Order)
        ProductionOrderEntity order = orderRepository.findById(execution.getProductionOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lệnh sản xuất liên quan."));

        // 3. Cập nhật thông tin thực thi
        BigDecimal goodQty = new BigDecimal(request.getGoodQty() != null ? request.getGoodQty() : 0);
        BigDecimal defectQty = new BigDecimal(request.getDefectQty() != null ? request.getDefectQty() : 0);

        execution.setGoodQuantity(goodQty);
        execution.setDefectQuantity(defectQty);
        execution.setEndTime(request.getFinishedAt() != null ? request.getFinishedAt().toInstant() : Instant.now());

        executionRepository.save(execution);

        // 4. Cập nhật trạng thái Lệnh sản xuất (Order)
        // Lưu ý: Trong thực tế bạn có thể cộng dồn số lượng đã hoàn thành
        // (completedQty) vào Order
        // Ở đây chúng ta cập nhật trạng thái COMPLETED nếu đây là bước cuối cùng hoặc
        // lệnh đã đủ hàng
        order.setStatus("COMPLETED");
        orderRepository.save(order);

        // 5. Phát sự kiện Kafka để các Service khác xử lý
        ProductionExecutionFinishedEvent finishedEvent = ProductionExecutionFinishedEvent.builder()
                .executionId(execution.getId())
                .orderId(order.getId())
                .productId(order.getProductId())
                .goodQuantity(goodQty)
                .defectQuantity(defectQty)
                .finishedAt(execution.getEndTime())
                .build();

        eventService.publishExecutionFinished(finishedEvent);

        log.info("Thực thi sản xuất hoàn tất. Thành phẩm: {}, Phế phẩm: {}", goodQty, defectQty);
        return FinishExecutionResponse.success();
    }

    @Override
    @Transactional
    public PauseExecutionResponse pauseExecution(PauseExecutionRequest request) {
        log.info("Yêu cầu tạm dừng thực thi: {}, Lý do: {}", request.getExecutionId(), request.getReason());

        // 1. Tìm bản ghi thực thi
        ProductionExecutionEntity execution = executionRepository.findById(UUID.fromString(request.getExecutionId()))
                .orElseThrow(
                        () -> new RuntimeException("Không tìm thấy thực thi sản xuất ID: " + request.getExecutionId()));

        // 2. Validate: Chỉ cho phép tạm dừng nếu chưa kết thúc và chưa bị tạm dừng
        // trước đó
        // (Giả định trạng thái RUNNING được xác định khi startTime != null và endTime
        // == null)
        if (execution.getEndTime() != null) {
            throw new IllegalStateException("Không thể tạm dừng thực thi đã hoàn thành.");
        }

        // 3. Cập nhật trạng thái (Dựa trên thiết kế Entity của bạn, bạn có thể bổ sung
        // trường status vào ProductionExecutionEntity)
        // Hiện tại, chúng ta thực hiện logic nghiệp vụ qua việc phát event và ghi log
        // lý do
        Instant now = Instant.now();

        // Ghi chú: Nếu Entity của bạn chưa có trường status, hãy cân nhắc thêm @Column
        // private String status;
        // Ở đây ta giả định logic trạng thái được quản lý nhất quán.

        // 4. Phát Event Kafka cho Workflow Service
        ProductionExecutionPausedEvent pausedEvent = ProductionExecutionPausedEvent.builder()
                .executionId(execution.getId())
                .reason(request.getReason())
                .pausedAt(now)
                .build();

        eventService.publishExecutionPaused(pausedEvent);

        // 5. Trả về Response
        PauseExecutionResponse response = new PauseExecutionResponse();
        response.setSuccess(true);
        response.setExecutionId(execution.getId().toString());
        response.setStatus("PAUSED");
        response.setPausedAt(Date.from(now));

        log.info("Đã tạm dừng thực thi {} thành công.", execution.getId());
        return response;
    }

    @Override
    @Transactional
    public ResumeExecutionResponse resumeExecution(ResumeExecutionRequest request) {
        log.info("Yêu cầu tiếp tục sản xuất cho thực thi ID: {}", request.getExecutionId());

        // 1. Tìm bản ghi thực thi
        ProductionExecutionEntity execution = executionRepository.findById(UUID.fromString(request.getExecutionId()))
                .orElseThrow(() -> new RuntimeException("Không tìm thấy dữ liệu thực thi sản xuất."));

        // 2. Kiểm tra tính hợp lệ: Chỉ được Resume nếu chưa kết thúc
        if (execution.getEndTime() != null) {
            throw new IllegalStateException("Thực thi đã kết thúc, không thể tiếp tục.");
        }

        // 3. Cập nhật thời điểm quay lại
        Instant now = Instant.now();
        // Cập nhật trạng thái trong Entity (nếu bạn đã thêm field status)
        // execution.setStatus("RUNNING");
        // executionRepository.save(execution);

        // 4. Phát Event Kafka cho Workflow Service để xóa các cảnh báo Downtime
        ProductionExecutionResumedEvent resumedEvent = ProductionExecutionResumedEvent.builder()
                .executionId(execution.getId())
                .resumedAt(now)
                .build();

        eventService.publishExecutionResumed(resumedEvent);

        // 5. Build Response trả về cho FE
        ResumeExecutionResponse response = new ResumeExecutionResponse();
        response.setSuccess(true);
        response.setExecutionId(execution.getId().toString());
        response.setStatus("RUNNING");
        response.setResumedAt(Date.from(now));

        log.info("Đã tiếp tục thực thi ID: {} thành công.", execution.getId());
        return response;
    }

    @Override
    public ExecutionDetailResponse getExecutionDetail(UUID executionId) {
        log.info("Truy vấn chi tiết thực thi sản xuất ID: {}", executionId);

        // 1. Tìm bản ghi thực thi (Execution)
        ProductionExecutionEntity execution = executionRepository.findById(executionId)
                .orElseThrow(
                        () -> new RuntimeException("Không tìm thấy dữ liệu thực thi sản xuất cho ID: " + executionId));

        // 2. Tìm Lệnh sản xuất (Order) để lấy ProductionPlanId
        ProductionOrderEntity order = orderRepository.findById(execution.getProductionOrderId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy lệnh sản xuất liên quan."));

        // 3. Map dữ liệu sang Response DTO
        ExecutionDetailResponse response = new ExecutionDetailResponse();
        response.setExecutionId(execution.getId().toString());
        response.setProductionPlanId(order.getProductionPlanId().toString());

        // Xác định trạng thái dựa trên các mốc thời gian (hoặc dựa trên trường status
        // nếu bạn đã thêm vào Entity)
        // Logic: Nếu có endTime -> COMPLETED, nếu không -> RUNNING (hoặc PAUSED nếu bạn
        // có logic lưu trạng thái pause)
        if (execution.getEndTime() != null) {
            response.setStatus("COMPLETED");
            response.setCompletedAt(Date.from(execution.getEndTime()));
        } else {
            // Mặc định là RUNNING nếu chưa kết thúc
            // Nếu hệ thống của bạn lưu vết Pause vào DB riêng, bạn có thể kiểm tra ở đây để
            // set status "PAUSED"
            response.setStatus("RUNNING");
            response.setCompletedAt(null);
        }

        // 4. Set các mốc thời gian
        response.setStartedAt(execution.getStartTime() != null ? Date.from(execution.getStartTime()) : null);

        // Giả sử pausedAt bạn lấy từ một bảng Log hoặc trạng thái lưu tạm (hiện tại
        // Entity chưa có trường này)
        response.setPausedAt(null);

        return response;
    }

}
