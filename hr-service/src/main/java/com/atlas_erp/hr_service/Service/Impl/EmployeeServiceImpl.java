package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Cache.serviceCache.EmployeeCacheService;
import com.atlas_erp.hr_service.Dto.EmployeeAssignmentDTO;
import com.atlas_erp.hr_service.Dto.EmployeeItemDTO;
import com.atlas_erp.hr_service.Dto.Request.Employee.*;
import com.atlas_erp.hr_service.Dto.Response.Employee.*;
import com.atlas_erp.hr_service.Entity.EmployeeAssignmentEntity;
import com.atlas_erp.hr_service.Entity.EmployeeEntity;
import com.atlas_erp.hr_service.Repository.EmployeeRepository;
import com.atlas_erp.hr_service.Repository.EmployeeAssignmentRepository;
import lombok.RequiredArgsConstructor;
import java.util.Optional;
import java.time.LocalDate;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import com.atlas_erp.hr_service.Service.EmployeeService;
import com.atlas_erp.hr_service.Event.dtoEvent.EmployeeCreatedEvent;
import com.atlas_erp.hr_service.Event.serviceEvent.EmployeeEventService;
import com.atlas_erp.hr_service.Event.dtoEvent.EmployeeUpdatedEvent;
import com.atlas_erp.hr_service.Event.dtoEvent.EmployeeMovedEvent;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.util.StringUtils;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

    private final EmployeeRepository employeeRepository;
    private final EmployeeAssignmentRepository assignmentRepository;
    private final EmployeeCacheService employeeCacheService;
    private final EmployeeEventService employeeEventService;

    @Override
    public EmployeeListResponse getEmployees(EmployeeListRequest request) {
        // 1. Kiểm tra Cache Redis
        String key = employeeCacheService.generateKey(request);
        EmployeeListResponse cached = employeeCacheService.getEmployeeList(key);
        if (cached != null)
            return cached;

        // 2. Thiết lập phân trang
        int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() : 1;
        int size = (request.getPageSize() != null && request.getPageSize() > 0) ? request.getPageSize() : 10;
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("employeeCode").ascending());

        // 3. Xây dựng bộ lọc động (Specification)
        Specification<EmployeeEntity> spec = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            // Lọc theo từ khóa (Mã NV, Họ tên, Email)
            if (StringUtils.hasText(request.getKeyword())) {
                String pattern = "%" + request.getKeyword().toLowerCase() + "%";
                predicates.add(cb.or(
                        cb.like(cb.lower(root.get("employeeCode")), pattern),
                        cb.like(cb.lower(root.get("fullName")), pattern),
                        cb.like(cb.lower(root.get("email")), pattern)));
            }

            // Lọc theo trạng thái
            if (StringUtils.hasText(request.getStatus())) {
                predicates.add(cb.equal(root.get("status"), request.getStatus()));
            }

            // Lọc theo Phòng ban/Tổ (Dựa trên bảng Assignment)
            if (StringUtils.hasText(request.getDepartmentId()) || StringUtils.hasText(request.getTeamId())) {
                // Join sang bảng EmployeeAssignmentEntity
                Join<EmployeeEntity, EmployeeAssignmentEntity> assignment = root.join("assignments", JoinType.LEFT);

                // Chỉ lọc những assignment đang có hiệu lực (toDate là null hoặc chưa tới hạn)
                predicates.add(cb.or(
                        cb.isNull(assignment.get("toDate")),
                        cb.greaterThanOrEqualTo(assignment.get("toDate"), LocalDate.now())));

                if (StringUtils.hasText(request.getDepartmentId())) {
                    predicates
                            .add(cb.equal(assignment.get("departmentId"), UUID.fromString(request.getDepartmentId())));
                }
                if (StringUtils.hasText(request.getTeamId())) {
                    predicates.add(cb.equal(assignment.get("teamId"), UUID.fromString(request.getTeamId())));
                }
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };

        // 4. Truy vấn Database
        Page<EmployeeEntity> employeePage = employeeRepository.findAll(spec, pageable);

        // 5. Mapping sang DTO
        List<EmployeeItemDTO> dtoList = employeePage.getContent().stream()
                .map(this::mapToItemDto)
                .collect(Collectors.toList());

        EmployeeListResponse response = new EmployeeListResponse();
        response.setList(dtoList);
        response.setTotal(employeePage.getTotalElements());

        // 6. Lưu kết quả vào Redis
        employeeCacheService.putEmployeeList(key, response);

        return response;
    }

    private EmployeeItemDTO mapToItemDto(EmployeeEntity entity) {
        EmployeeItemDTO dto = new EmployeeItemDTO();
        dto.setId(entity.getId().toString());
        dto.setEmployeeCode(entity.getEmployeeCode());
        dto.setFullName(entity.getFullName());
        dto.setGender(entity.getGender());
        dto.setBirthDate(entity.getDob());
        dto.setPhone(entity.getPhone());
        dto.setEmail(entity.getEmail());
        dto.setStatus(entity.getStatus());

        // Tìm thông tin vị trí công tác hiện tại (Assignment)
        // Giả sử trong EmployeeEntity bạn có: @OneToMany List<EmployeeAssignmentEntity>
        // assignments
        if (entity.getAssignments() != null) {
            entity.getAssignments().stream()
                    .filter(a -> a.getToDate() == null || a.getToDate().isAfter(LocalDate.now()))
                    .findFirst()
                    .ifPresent(a -> {
                        dto.setDepartmentId(a.getDepartmentId().toString());
                        dto.setTeamId(a.getTeamId() != null ? a.getTeamId().toString() : null);
                        dto.setPosition(a.getPosition());
                    });
        }

        return dto;
    }

    @Override
    @Transactional // Rất quan trọng: Đảm bảo tạo Employee và Assignment cùng thành công hoặc cùng
                   // thất bại
    public CreateEmployeeResponse createEmployee(CreateEmployeeRequest request) {
        // 1. Kiểm tra mã nhân viên đã tồn tại chưa (Internal check)
        if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
            throw new IllegalStateException("Mã nhân viên " + request.getEmployeeCode() + " đã tồn tại.");
        }

        // 2. Tạo thực thể Employee (Thông tin cơ bản)
        EmployeeEntity employee = new EmployeeEntity();
        employee.setEmployeeCode(request.getEmployeeCode());
        employee.setFullName(request.getFullName());
        employee.setGender(request.getGender());
        employee.setDob(request.getBirthDate());
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setHireDate(request.getJoinDate());
        employee.setStatus(request.getStatus() != null ? request.getStatus() : "ACTIVE");
        employee.setCreatedAt(Instant.now());

        employee = employeeRepository.save(employee);

        // 3. Tạo thực thể Assignment (Thông tin vị trí công tác)
        // ERP cần cái này để biết nhân viên thuộc phòng ban nào ngay khi vào công ty
        EmployeeAssignmentEntity assignment = new EmployeeAssignmentEntity();
        assignment.setEmployeeId(employee.getId());
        assignment.setDepartmentId(UUID.fromString(request.getDepartmentId()));
        if (request.getTeamId() != null) {
            assignment.setTeamId(UUID.fromString(request.getTeamId()));
        }
        assignment.setPosition(request.getPosition());
        assignment.setFromDate(request.getJoinDate()); // Ngày bắt đầu bổ nhiệm = ngày vào làm

        assignmentRepository.save(assignment);

        // 4. Xóa Cache danh sách nhân viên (Vì có dữ liệu mới)
        employeeCacheService.evictAllList();

        // 5. Phát Event (Để Payroll/Audit/IAM xử lý bất đồng bộ)
        EmployeeCreatedEvent event = EmployeeCreatedEvent.builder()
                .employeeId(employee.getId())
                .employeeCode(employee.getEmployeeCode())
                .email(employee.getEmail())
                .departmentId(assignment.getDepartmentId())
                .teamId(assignment.getTeamId())
                .joinDate(employee.getHireDate())
                .build();

        employeeEventService.publishEmployeeCreatedEvent(event);

        // 6. Trả về kết quả
        CreateEmployeeResponse response = new CreateEmployeeResponse();
        response.setId(employee.getId().toString());
        return response;
    }

    @Override
    @Transactional
    public UpdateEmployeeResponse updateEmployee(UUID id, UpdateEmployeeRequest request) {
        // 1. Kiểm tra tồn tại
        EmployeeEntity employee = employeeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " + id));

        // 2. Cập nhật thông tin cơ bản
        employee.setFullName(request.getFullName());
        employee.setPhone(request.getPhone());
        employee.setEmail(request.getEmail());
        employee.setStatus(request.getStatus());

        employeeRepository.save(employee);

        // 3. Xử lý điều chuyển bộ phận (Department/Team/Position)
        // Lấy Assignment hiện tại (đang có hiệu lực)
        Optional<EmployeeAssignmentEntity> currentAssignmentOpt = assignmentRepository.findAll().stream()
                .filter(a -> a.getEmployeeId().equals(id)
                        && (a.getToDate() == null || a.getToDate().isAfter(LocalDate.now())))
                .findFirst();

        if (currentAssignmentOpt.isPresent()) {
            EmployeeAssignmentEntity current = currentAssignmentOpt.get();

            // Kiểm tra xem có sự thay đổi về bộ phận không
            boolean isChanged = !current.getDepartmentId().toString().equals(request.getDepartmentId())
                    || (current.getTeamId() != null && !current.getTeamId().toString().equals(request.getTeamId()))
                    || !current.getPosition().equals(request.getPosition());

            if (isChanged) {
                // Kết thúc assignment cũ
                current.setToDate(LocalDate.now());
                assignmentRepository.save(current);

                // Tạo assignment mới (Bắt đầu từ ngày mai)
                EmployeeAssignmentEntity newAssignment = new EmployeeAssignmentEntity();
                newAssignment.setEmployeeId(id);
                newAssignment.setDepartmentId(UUID.fromString(request.getDepartmentId()));
                if (request.getTeamId() != null) {
                    newAssignment.setTeamId(UUID.fromString(request.getTeamId()));
                }
                newAssignment.setPosition(request.getPosition());
                newAssignment.setFromDate(LocalDate.now().plusDays(1));

                assignmentRepository.save(newAssignment);
            }
        }

        // 4. Xóa Cache (List và Detail của nhân viên này)
        employeeCacheService.evictAllList();
        // employeeCacheService.evictDetail(id); // Nếu bạn có cache detail

        // 5. Phát Event để IAM sync (ví dụ: đổi email hoặc lock tài khoản)
        EmployeeUpdatedEvent event = EmployeeUpdatedEvent.builder()
                .employeeId(id)
                .fullName(employee.getFullName())
                .email(employee.getEmail())
                .status(employee.getStatus())
                .departmentId(UUID.fromString(request.getDepartmentId()))
                .build();
        employeeEventService.publishEmployeeUpdatedEvent(event);

        return UpdateEmployeeResponse.success();
    }

    @Override
    public EmployeeDetailResponse getEmployeeDetail(UUID employeeId) {
        // 1. THỬ LẤY TỪ CACHE TRƯỚC
        EmployeeDetailResponse cached = employeeCacheService.getDetail(employeeId);
        if (cached != null) {
            return cached; // Nếu có trong Redis thì trả về luôn, không xuống DB
        }

        // 2. NẾU KHÔNG CÓ TRONG CACHE -> TRUY VẤN DATABASE
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " + employeeId));

        // 3. MAPPING DỮ LIỆU
        EmployeeDetailResponse response = new EmployeeDetailResponse();
        response.setEmployeeId(employee.getId().toString());
        response.setEmployeeCode(employee.getEmployeeCode());
        response.setFullName(employee.getFullName());
        response.setDateOfBirth(employee.getDob());
        response.setGender(employee.getGender());
        response.setPhone(employee.getPhone());
        response.setEmail(employee.getEmail());
        response.setStatus(employee.getStatus());
        response.setHiredDate(employee.getHireDate());

        if (employee.getAssignments() != null) {
            employee.getAssignments().stream()
                    .filter(a -> a.getToDate() == null || a.getToDate().isAfter(LocalDate.now()))
                    .findFirst()
                    .ifPresent(a -> {
                        response.setCurrentDepartmentId(a.getDepartmentId().toString());
                        response.setCurrentTeamId(a.getTeamId() != null ? a.getTeamId().toString() : null);
                    });
        }

        // 4. GHI VÀO CACHE CHO LẦN SAU (Lưu trong 60 phút)
        employeeCacheService.putDetail(employeeId, response);

        return response;
    }

    @Override
    public EmployeeAssignmentHistoryResponse getAssignmentHistory(UUID employeeId) {
        // 1. KIỂM TRA CACHE TRƯỚC
        EmployeeAssignmentHistoryResponse cached = employeeCacheService.getAssignmentHistory(employeeId);
        if (cached != null) {
            return cached;
        }

        // 2. NẾU CACHE MISS -> TRUY VẤN DB
        if (!employeeRepository.existsById(employeeId)) {
            throw new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " + employeeId);
        }

        List<EmployeeAssignmentEntity> entities = assignmentRepository.findByEmployeeIdOrderByFromDateDesc(employeeId);

        // 3. MAPPING SANG DTO
        List<EmployeeAssignmentDTO> dtoList = entities.stream().map(entity -> {
            EmployeeAssignmentDTO dto = new EmployeeAssignmentDTO();
            dto.setDepartmentId(entity.getDepartmentId().toString());
            dto.setTeamId(entity.getTeamId() != null ? entity.getTeamId().toString() : null);
            dto.setFromDate(entity.getFromDate());
            dto.setToDate(entity.getToDate());
            dto.setReason(entity.getPosition()); // Map tạm position vào reason nếu DB chưa có field reason
            return dto;
        }).collect(Collectors.toList());

        EmployeeAssignmentHistoryResponse response = new EmployeeAssignmentHistoryResponse();
        response.setEmployeeId(employeeId.toString());
        response.setAssignments(dtoList);

        // 4. GHI VÀO CACHE CHO LẦN SAU
        employeeCacheService.putAssignmentHistory(employeeId, response);

        return response;
    }

    @Override
    @Transactional
    public EmployeeTransferResponse transferEmployee(UUID employeeId, EmployeeTransferRequest request) {
        // 1. Kiểm tra nhân viên tồn tại
        EmployeeEntity employee = employeeRepository.findById(employeeId)
                .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên: " + employeeId));

        // 2. Xử lý logic Assignment (Kết thúc cái cũ, tạo cái mới)
        // Tìm assignment hiện tại đang active
        Optional<EmployeeAssignmentEntity> currentAssignmentOpt = assignmentRepository.findAll().stream()
                .filter(a -> a.getEmployeeId().equals(employeeId)
                        && (a.getToDate() == null || a.getToDate().isAfter(LocalDate.now())))
                .findFirst();

        if (currentAssignmentOpt.isPresent()) {
            EmployeeAssignmentEntity current = currentAssignmentOpt.get();
            // Ngày kết thúc assignment cũ là ngày trước ngày hiệu lực mới
            current.setToDate(request.getEffectiveDate().minusDays(1));
            assignmentRepository.save(current);
        }

        // Tạo Assignment mới cho đơn vị mới
        EmployeeAssignmentEntity newAssignment = new EmployeeAssignmentEntity();
        newAssignment.setEmployeeId(employeeId);
        newAssignment.setDepartmentId(UUID.fromString(request.getToDepartmentId()));
        if (request.getToTeamId() != null) {
            newAssignment.setTeamId(UUID.fromString(request.getToTeamId()));
        }
        newAssignment.setFromDate(request.getEffectiveDate());
        // Giả sử lấy position cũ sang, hoặc bạn có thể thêm field position vào Request
        newAssignment.setPosition("STAFF");

        assignmentRepository.save(newAssignment);

        // 3. Xóa Cache liên quan
        employeeCacheService.evictAllList();
        employeeCacheService.evictDetail(employeeId);
        employeeCacheService.evictAssignmentHistory(employeeId);

        // 4. Phát Event EmployeeMovedEvent (Dùng cho IAM, Production, Payroll)
        EmployeeMovedEvent event = EmployeeMovedEvent.builder()
                .employeeId(employeeId)
                .fromDepartmentId(UUID.fromString(request.getFromDepartmentId()))
                .toDepartmentId(UUID.fromString(request.getToDepartmentId()))
                .effectiveDate(request.getEffectiveDate())
                .build();
        employeeEventService.publishEmployeeMovedEvent(event);

        // 5. Trả về response
        EmployeeTransferResponse response = new EmployeeTransferResponse();
        response.setSuccess(true);
        response.setEmployeeId(employeeId.toString());
        response.setTransferId(UUID.randomUUID().toString()); // Tạo ID giao dịch điều chuyển
        response.setStatus("COMPLETED");
        response.setEffectiveDate(request.getEffectiveDate());

        return response;
    }

}
