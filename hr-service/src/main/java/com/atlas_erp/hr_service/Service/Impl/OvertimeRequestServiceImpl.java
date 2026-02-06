package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Dto.Request.OvertimeRequest.CreateOvertimeRequest;
import com.atlas_erp.hr_service.Dto.Response.OvertimeRequest.CreateOvertimeResponse;
import com.atlas_erp.hr_service.Entity.OvertimeRequestEntity;
import com.atlas_erp.hr_service.Event.dtoEvent.OvertimeRequestCreatedEvent;
import com.atlas_erp.hr_service.Event.serviceEvent.OvertimeEventService;
import com.atlas_erp.hr_service.Repository.EmployeeRepository;
import com.atlas_erp.hr_service.Repository.OvertimeRequestRepository;
import com.atlas_erp.hr_service.Service.OvertimeRequestService;
import com.atlas_erp.hr_service.Exception.ResourceNotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class OvertimeRequestServiceImpl implements OvertimeRequestService {

    private final EmployeeRepository employeeRepository;
    private final OvertimeRequestRepository overtimeRequestRepository;
    private final OvertimeEventService overtimeEventService;

    @Override
    @Transactional
    public CreateOvertimeResponse createOvertimeRequest(CreateOvertimeRequest request) {
        UUID empId = UUID.fromString(request.getEmployeeId());

        // 1. Kiểm tra nhân viên tồn tại
        if (!employeeRepository.existsById(empId)) {
            throw new ResourceNotFoundException("Nhân viên không tồn tại: " + empId);
        }

        // 2. Validate thời gian: Giờ kết thúc phải sau giờ bắt đầu
        if (!request.getToTime().isAfter(request.getFromTime())) {
            throw new IllegalArgumentException("Giờ kết thúc tăng ca phải sau giờ bắt đầu.");
        }

        // 3. Giả lập gọi Workflow Service để lấy Instance ID
        String mockWorkflowId = "WF-OT-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 4. Lưu vào Database
        OvertimeRequestEntity otRequest = new OvertimeRequestEntity();
        otRequest.setEmployeeId(empId);
        otRequest.setOtDate(request.getOtDate());
        otRequest.setFromTime(request.getFromTime());
        otRequest.setToTime(request.getToTime());
        otRequest.setReason(request.getReason());
        otRequest.setStatus("PENDING");
        otRequest.setWorkflowId(mockWorkflowId);
        otRequest.setCreatedAt(Instant.now());

        overtimeRequestRepository.save(otRequest);

        // 5. Phát Event Kafka cho Payroll và Workflow Service
        OvertimeRequestCreatedEvent event = OvertimeRequestCreatedEvent.builder()
                .requestId(otRequest.getId())
                .employeeId(empId)
                .otDate(request.getOtDate())
                .fromTime(request.getFromTime())
                .toTime(request.getToTime())
                .workflowId(mockWorkflowId)
                .build();

        overtimeEventService.publishOvertimeRequested(event);

        // 6. Trả về Response
        CreateOvertimeResponse response = new CreateOvertimeResponse();
        response.setWorkflowId(mockWorkflowId);

        log.info("Đã tạo yêu cầu tăng ca cho nhân viên {} vào ngày {}", empId, request.getOtDate());
        return response;
    }

}
