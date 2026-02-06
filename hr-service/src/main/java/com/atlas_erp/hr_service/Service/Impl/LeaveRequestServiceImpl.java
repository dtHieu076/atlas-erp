package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Dto.Request.LeaveRequest.CreateLeaveRequest;
import com.atlas_erp.hr_service.Dto.Response.LeaveRequest.CreateLeaveResponse;
import com.atlas_erp.hr_service.Entity.LeaveRequestEntity;
import com.atlas_erp.hr_service.Event.dtoEvent.LeaveRequestCreatedEvent;
import com.atlas_erp.hr_service.Event.serviceEvent.LeaveEventService;
import com.atlas_erp.hr_service.Repository.EmployeeRepository;
import com.atlas_erp.hr_service.Repository.LeaveRequestRepository;
import com.atlas_erp.hr_service.Service.LeaveRequestService;
import com.atlas_erp.hr_service.Exception.ResourceNotFoundException;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j // Giải quyết lỗi "log cannot be resolved"
public class LeaveRequestServiceImpl implements LeaveRequestService {

    // Khai báo các Repository và Service để giải quyết lỗi "cannot be resolved"
    private final EmployeeRepository employeeRepository;
    private final LeaveRequestRepository leaveRequestRepository;
    private final LeaveEventService leaveEventService;

    @Override
    @Transactional // Giải quyết lỗi "Transactional cannot be resolved"
    public CreateLeaveResponse createLeaveRequest(CreateLeaveRequest request) {
        // Giải quyết lỗi "UUID cannot be resolved"
        UUID empId = UUID.fromString(request.getEmployeeId());

        // 1. Kiểm tra nhân viên tồn tại
        if (!employeeRepository.existsById(empId)) {
            throw new ResourceNotFoundException("Nhân viên không tồn tại: " + empId);
        }

        // 2. Validate logic ngày tháng
        if (request.getFromDate().isAfter(request.getToDate())) {
            throw new IllegalArgumentException("Ngày bắt đầu không được sau ngày kết thúc.");
        }

        // 3. Kiểm tra chồng lấn (Overlap)
        boolean hasOverlap = leaveRequestRepository.existsByEmployeeIdAndStatusInAndDateRange(
                empId,
                List.of("PENDING", "APPROVED"),
                request.getFromDate(),
                request.getToDate());

        if (hasOverlap) {
            throw new IllegalStateException("Đã có đơn nghỉ phép khác trùng lặp trong khoảng thời gian này.");
        }

        // 4. Giả lập Workflow ID
        String generatedWorkflowId = "WF-LEAVE-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

        // 5. Lưu đơn nghỉ
        LeaveRequestEntity leaveRequest = new LeaveRequestEntity();
        leaveRequest.setEmployeeId(empId);
        leaveRequest.setLeaveType(request.getLeaveType());
        leaveRequest.setFromDate(request.getFromDate());
        leaveRequest.setToDate(request.getToDate());
        leaveRequest.setReason(request.getReason());
        leaveRequest.setStatus("PENDING");
        leaveRequest.setWorkflowId(generatedWorkflowId);
        leaveRequest.setCreatedAt(Instant.now());

        leaveRequestRepository.save(leaveRequest);

        // 6. Phát Event Kafka
        LeaveRequestCreatedEvent event = LeaveRequestCreatedEvent.builder()
                .leaveRequestId(leaveRequest.getId())
                .employeeId(empId)
                .leaveType(request.getLeaveType())
                .fromDate(request.getFromDate())
                .toDate(request.getToDate())
                .workflowId(generatedWorkflowId)
                .reason(request.getReason())
                .build();

        leaveEventService.publishLeaveRequestCreated(event);

        // 7. Trả về Response
        CreateLeaveResponse response = new CreateLeaveResponse();
        response.setWorkflowId(generatedWorkflowId);

        log.info("Tạo đơn nghỉ thành công cho nhân viên: {}", empId);
        return response;
    }
}