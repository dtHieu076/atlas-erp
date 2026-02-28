package com.atlas_erp.hr_service.Service.Impl;

import com.atlas_erp.hr_service.Cache.serviceCache.EmployeeCacheService;
import com.atlas_erp.hr_service.Dto.Request.Employee.*;
import com.atlas_erp.hr_service.Dto.Response.Employee.*;
import com.atlas_erp.hr_service.Dto.Response.ApiResponse;
import com.atlas_erp.hr_service.Dto.Response.Employee_v0.*;
import com.atlas_erp.hr_service.Entity.EmployeeAssignmentEntity;
import com.atlas_erp.hr_service.Entity.EmployeeEntity;
import com.atlas_erp.hr_service.Repository.EmployeeRepository;
import com.atlas_erp.hr_service.Repository.EmployeeSkillRepository;
import com.atlas_erp.hr_service.Repository.EmployeeAssignmentRepository;
import com.atlas_erp.hr_service.Repository.EmployeeContractRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import com.atlas_erp.hr_service.Service.EmployeeService;
import java.util.List;
import java.util.UUID;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class EmployeeServiceImpl implements EmployeeService {

        private final EmployeeRepository employeeRepository;
        private final EmployeeAssignmentRepository assignmentRepository;
        private final EmployeeContractRepository contractRepository;
        private final EmployeeSkillRepository skillRepository;

        private final EmployeeCacheService employeeCacheService;
        // private final EmployeeEventService employeeEventService;

        @Override
        public List<EmployeeResponse> getAllEmployees() {

                // 1. Check cache trước
                // List<EmployeeResponse> cached = employeeCacheService.getAllEmployees();

                // if (cached != null) {
                //         return cached;
                // }

                // 2. Nếu không có cache → query DB
                List<EmployeeResponse> data = employeeRepository.findAll()
                                .stream()
                                .map(this::mapToEmployeeResponse)
                                .toList();

                // 3. Lưu cache 60 phút
                // employeeCacheService.putAllEmployees(data);

                return data;
        }

        @Override
        public EmployeeProfileResponse getEmployeeProfile(UUID id) {

                // 1. Check cache
                // EmployeeProfileResponse cached = employeeCacheService.getEmployeeProfile(id);
                // if (cached != null) {
                //         return cached;
                // }

                // 2. Load employee
                EmployeeEntity employee = employeeRepository.findById(id)
                                .orElseThrow(() -> new RuntimeException("Employee not found"));

                // ===== Manager =====
                ManagerDTO managerDTO = null;
                if (employee.getManagerId() != null) {

                        EmployeeEntity manager = employeeRepository
                                        .findById(employee.getManagerId())
                                        .orElse(null);

                        if (manager != null) {

                                String managerJobTitle = assignmentRepository
                                                .findTopByEmployeeIdAndToDateIsNullOrderByFromDateDesc(manager.getId())
                                                .map(EmployeeAssignmentEntity::getJobTitleName)
                                                .orElse(null);

                                managerDTO = ManagerDTO.builder()
                                                .id(manager.getId())
                                                .fullName(manager.getFullName())
                                                .jobTitleName(managerJobTitle)
                                                .build();
                        }
                }

                // ===== Personal =====
                EmployeeProfileResponse.PersonalInfo personal = EmployeeProfileResponse.PersonalInfo.builder()
                                .dob(employee.getDob())
                                .nationalId(employee.getNationalId())
                                .permanentAddress(employee.getPermanentAddress())
                                .currentAddress(employee.getCurrentAddress())
                                .hometown(employee.getHometown())
                                .personalEmail(employee.getPersonalEmail())
                                .workEmail(employee.getWorkEmail())
                                .phone(employee.getPhone())
                                .maritalStatus(employee.getMaritalStatus())
                                .dependentsCount(employee.getDependentsCount())
                                .build();

                // ===== Current Assignment (Job) =====
                EmployeeAssignmentEntity currentAssignment = assignmentRepository
                                .findTopByEmployeeIdAndToDateIsNullOrderByFromDateDesc(employee.getId())
                                .orElse(null);

                EmployeeProfileResponse.JobInfo job = null;

                if (currentAssignment != null) {
                        job = EmployeeProfileResponse.JobInfo.builder()
                                        .departmentId(currentAssignment.getDepartmentId())
                                        .departmentName(currentAssignment.getDepartmentName())
                                        .teamId(currentAssignment.getTeamId())
                                        .teamName(currentAssignment.getTeamName())
                                        .jobTitleId(currentAssignment.getJobTitleId())
                                        .jobTitleName(currentAssignment.getJobTitleName())
                                        .gradeLevel(currentAssignment.getGradeLevel())
                                        .employmentType(employee.getEmploymentType())
                                        .joinDate(employee.getHireDate())
                                        .probationEndDate(employee.getProbationEndDate())
                                        .build();
                }

                // ===== History =====
                List<AssignmentHistoryResponse> history = assignmentRepository.findByEmployeeId(employee.getId())
                                .stream()
                                .map(a -> AssignmentHistoryResponse.builder()
                                                .departmentId(a.getDepartmentId())
                                                .departmentName(a.getDepartmentName())
                                                .jobTitleId(a.getJobTitleId())
                                                .jobTitleName(a.getJobTitleName())
                                                .fromDate(a.getFromDate())
                                                .toDate(a.getToDate())
                                                .changeReason(a.getChangeReason())
                                                .build())
                                .toList();

                // ===== Contracts =====
                List<ContractResponse> contracts = contractRepository.findByEmployeeId(employee.getId())
                                .stream()
                                .map(c -> ContractResponse.builder()
                                                .id(c.getId())
                                                .contractTemplateId(c.getContractTemplateId())
                                                .contractTemplateName(c.getContractTemplateName())
                                                .contractNo(c.getContractNo())
                                                .startDate(c.getStartDate())
                                                .endDate(c.getEndDate())
                                                .baseSalary(c.getBaseSalary())
                                                .status(c.getStatus())
                                                .build())
                                .toList();

                // ===== Skills =====
                List<SkillResponse> skills = skillRepository.findByEmployeeId(employee.getId())
                                .stream()
                                .map(s -> SkillResponse.builder()
                                                .skillId(s.getSkill().getId())
                                                .skillName(s.getSkill().getName())
                                                .level(s.getCurrentLevel())
                                                .certified(s.getCertified())
                                                .certifiedDate(s.getCertifiedDate())
                                                .build())
                                .toList();

                // ===== Final build (ROOT CLEAN) =====
                EmployeeProfileResponse profile = EmployeeProfileResponse.builder()
                                .id(employee.getId())
                                .employeeCode(employee.getEmployeeCode())
                                .fullName(employee.getFullName())
                                .avatarUrl(employee.getAvatarUrl())
                                .gender(employee.getGender())
                                .status(employee.getStatus())
                                .manager(managerDTO)
                                .personal(personal)
                                .job(job)
                                .history(history)
                                .skills(skills)
                                .contracts(contracts)
                                .build();

                // Save cache
                // employeeCacheService.putEmployeeProfile(id, profile);

                return profile;
        }

        private EmployeeResponse mapToEmployeeResponse(EmployeeEntity e) {

                // Lấy assignment hiện tại (toDate = null)
                EmployeeAssignmentEntity current = assignmentRepository
                                .findTopByEmployeeIdAndToDateIsNullOrderByFromDateDesc(e.getId())
                                .orElse(null);

                return EmployeeResponse.builder()
                                .id(e.getId())
                                .employeeCode(e.getEmployeeCode())
                                .fullName(e.getFullName())
                                .avatarUrl(e.getAvatarUrl())
                                .gender(e.getGender())
                                .departmentId(current != null ? current.getDepartmentId() : null)
                                .departmentName(current != null ? current.getDepartmentName() : null)
                                .teamId(current != null ? current.getTeamId() : null)
                                .teamName(current != null ? current.getTeamName() : null)
                                .jobTitleId(current != null ? current.getJobTitleId() : null)
                                .jobTitleName(current != null ? current.getJobTitleName() : null)
                                .gradeLevel(current != null ? current.getGradeLevel() : null)
                                .hireDate(e.getHireDate())
                                .employmentType(e.getEmploymentType())
                                .status(e.getStatus())
                                .phone(e.getPhone())
                                .workEmail(e.getWorkEmail())
                                .build();
        }

        @Override
        public ApiResponse<List<EmployeeSummaryItemDTO>> getEmployeeSummaryBatch(
                        EmployeeBatchRequest request) {

                // List<EmployeeEntity> employees =
                // employeeRepository.findAllWithAssignmentsByIdIn(request.getIds());

                // List<EmployeeSummaryItemDTO> result = employees.stream()
                // .map(employee -> {

                // UUID departmentId = employee.getAssignments()
                // .stream()
                // .filter(a -> a.getToDate() == null)
                // .max(Comparator.comparing(a -> a.getFromDate() != null
                // ? a.getFromDate()
                // : LocalDate.MIN))
                // .map(EmployeeAssignmentEntity::getDepartmentId)
                // .orElse(null);

                // return EmployeeSummaryItemDTO.builder()
                // .id(employee.getId())
                // .employeeCode(employee.getEmployeeCode())
                // .fullName(employee.getFullName())
                // .email(employee.getEmail())
                // .departmentId(departmentId)
                // .build();
                // })
                // .toList();

                // return ApiResponse.success(
                // "Lấy danh sách nhân viên thành công",
                // result);
                return null;
        }

        @Override
        @Transactional // Rất quan trọng: Đảm bảo tạo Employee và Assignment cùng thành công hoặc cùng
                       // thất bại
        public CreateEmployeeResponse createEmployee(CreateEmployeeRequest request) {
                // // 1. Kiểm tra mã nhân viên đã tồn tại chưa (Internal check)
                // if (employeeRepository.existsByEmployeeCode(request.getEmployeeCode())) {
                // throw new IllegalStateException("Mã nhân viên " + request.getEmployeeCode() +
                // " đã tồn tại.");
                // }

                // // 2. Tạo thực thể Employee (Thông tin cơ bản)
                // EmployeeEntity employee = new EmployeeEntity();
                // employee.setEmployeeCode(request.getEmployeeCode());
                // employee.setFullName(request.getFullName());
                // employee.setGender(request.getGender());
                // employee.setDob(request.getBirthDate());
                // employee.setPhone(request.getPhone());
                // employee.setEmail(request.getEmail());
                // employee.setHireDate(request.getJoinDate());
                // employee.setStatus(request.getStatus() != null ? request.getStatus() :
                // "ACTIVE");
                // employee.setCreatedAt(Instant.now());

                // employee = employeeRepository.save(employee);

                // // 3. Tạo thực thể Assignment (Thông tin vị trí công tác)
                // // ERP cần cái này để biết nhân viên thuộc phòng ban nào ngay khi vào công ty
                // EmployeeAssignmentEntity assignment = new EmployeeAssignmentEntity();
                // assignment.setEmployeeId(employee.getId());
                // assignment.setDepartmentId(UUID.fromString(request.getDepartmentId()));
                // if (request.getTeamId() != null) {
                // assignment.setTeamId(UUID.fromString(request.getTeamId()));
                // }
                // assignment.setPosition(request.getPosition());
                // assignment.setFromDate(request.getJoinDate()); // Ngày bắt đầu bổ nhiệm =
                // ngày vào làm

                // assignmentRepository.save(assignment);

                // // 4. Xóa Cache danh sách nhân viên (Vì có dữ liệu mới)
                employeeCacheService.evictAllEmployees();

                // // 5. Phát Event (Để Payroll/Audit/IAM xử lý bất đồng bộ)
                // EmployeeCreatedEvent event = EmployeeCreatedEvent.builder()
                // .employeeId(employee.getId())
                // .employeeCode(employee.getEmployeeCode())
                // .email(employee.getEmail())
                // .departmentId(assignment.getDepartmentId())
                // .teamId(assignment.getTeamId())
                // .joinDate(employee.getHireDate())
                // .build();

                // employeeEventService.publishEmployeeCreatedEvent(event);

                // // 6. Trả về kết quả
                // CreateEmployeeResponse response = new CreateEmployeeResponse();
                // response.setId(employee.getId().toString());
                // return response;
                return null;
        }

        @Override
        @Transactional
        public UpdateEmployeeResponse updateEmployee(UUID id, UpdateEmployeeRequest request) {
                // // 1. Kiểm tra tồn tại
                // EmployeeEntity employee = employeeRepository.findById(id)
                // .orElseThrow(() -> new ResourceNotFoundException("Không tìm thấy nhân viên
                // với ID: " + id));

                // // 2. Cập nhật thông tin cơ bản
                // employee.setFullName(request.getFullName());
                // employee.setPhone(request.getPhone());
                // employee.setEmail(request.getEmail());
                // employee.setStatus(request.getStatus());

                // employeeRepository.save(employee);

                // // 3. Xử lý điều chuyển bộ phận (Department/Team/Position)
                // // Lấy Assignment hiện tại (đang có hiệu lực)
                // Optional<EmployeeAssignmentEntity> currentAssignmentOpt =
                // assignmentRepository.findAll().stream()
                // .filter(a -> a.getEmployeeId().equals(id)
                // && (a.getToDate() == null || a.getToDate().isAfter(LocalDate.now())))
                // .findFirst();

                // if (currentAssignmentOpt.isPresent()) {
                // EmployeeAssignmentEntity current = currentAssignmentOpt.get();

                // // Kiểm tra xem có sự thay đổi về bộ phận không
                // boolean isChanged =
                // !current.getDepartmentId().toString().equals(request.getDepartmentId())
                // || (current.getTeamId() != null &&
                // !current.getTeamId().toString().equals(request.getTeamId()))
                // || !current.getPosition().equals(request.getPosition());

                // if (isChanged) {
                // // Kết thúc assignment cũ
                // current.setToDate(LocalDate.now());
                // assignmentRepository.save(current);

                // // Tạo assignment mới (Bắt đầu từ ngày mai)
                // EmployeeAssignmentEntity newAssignment = new EmployeeAssignmentEntity();
                // newAssignment.setEmployeeId(id);
                // newAssignment.setDepartmentId(UUID.fromString(request.getDepartmentId()));
                // if (request.getTeamId() != null) {
                // newAssignment.setTeamId(UUID.fromString(request.getTeamId()));
                // }
                // newAssignment.setPosition(request.getPosition());
                // newAssignment.setFromDate(LocalDate.now().plusDays(1));

                // assignmentRepository.save(newAssignment);
                // }
                // }

                // // 4. Xóa Cache (List và Detail của nhân viên này)
                employeeCacheService.evictAllEmployees();
                employeeCacheService.evictEmployeeProfile(id); // cũng xóa detail nếu tồn tại

                // // 5. Phát Event để IAM sync (ví dụ: đổi email hoặc lock tài khoản)
                // EmployeeUpdatedEvent event = EmployeeUpdatedEvent.builder()
                // .employeeId(id)
                // .fullName(employee.getFullName())
                // .email(employee.getEmail())
                // .status(employee.getStatus())
                // .departmentId(UUID.fromString(request.getDepartmentId()))
                // .build();
                // employeeEventService.publishEmployeeUpdatedEvent(event);

                return UpdateEmployeeResponse.success();
        }

        @Override
        public EmployeeAssignmentHistoryResponse getAssignmentHistory(UUID employeeId) {
                // // 1. KIỂM TRA CACHE TRƯỚC
                // EmployeeAssignmentHistoryResponse cached =
                // employeeCacheService.getAssignmentHistory(employeeId);
                // if (cached != null) {
                // return cached;
                // }

                // // 2. NẾU CACHE MISS -> TRUY VẤN DB
                // if (!employeeRepository.existsById(employeeId)) {
                // throw new ResourceNotFoundException("Không tìm thấy nhân viên với ID: " +
                // employeeId);
                // }

                // List<EmployeeAssignmentEntity> entities =
                // assignmentRepository.findByEmployeeIdOrderByFromDateDesc(employeeId);

                // // 3. MAPPING SANG DTO
                // List<EmployeeAssignmentDTO> dtoList = entities.stream().map(entity -> {
                // EmployeeAssignmentDTO dto = new EmployeeAssignmentDTO();
                // dto.setDepartmentId(entity.getDepartmentId().toString());
                // dto.setTeamId(entity.getTeamId() != null ? entity.getTeamId().toString() :
                // null);
                // dto.setFromDate(entity.getFromDate());
                // dto.setToDate(entity.getToDate());
                // dto.setReason(entity.getPosition()); // Map tạm position vào reason nếu DB
                // chưa có field reason
                // return dto;
                // }).collect(Collectors.toList());

                // EmployeeAssignmentHistoryResponse response = new
                // EmployeeAssignmentHistoryResponse();
                // response.setEmployeeId(employeeId.toString());
                // response.setAssignments(dtoList);

                // // 4. GHI VÀO CACHE CHO LẦN SAU
                // employeeCacheService.putAssignmentHistory(employeeId, response);

                // return response;
                return null;
        }

        @Override
        @Transactional
        public EmployeeTransferResponse transferEmployee(UUID employeeId, EmployeeTransferRequest request) {
                return null;
        }

        @Override
        public EmployeeStatisticsResponse getEmployeeStatistics(EmployeeStatisticsRequest request) {
                // // 1. Xử lý thời gian thực tế từ chuỗi period (yyyy-MM)
                // YearMonth ym = YearMonth.parse(request.getPeriod());
                // LocalDate startDate = ym.atDay(1);
                // LocalDate endDate = ym.atEndOfMonth();

                // UUID deptId = (request.getDepartmentId() != null) ?
                // UUID.fromString(request.getDepartmentId()) : null;

                // // 2. Truy vấn dữ liệu THẬT từ DB
                // long activeEmps = employeeRepository.countActiveEmployees(deptId);
                // long newHires = employeeRepository.countNewHires(startDate, endDate, deptId);
                // BigDecimal totalOT = attendanceRepository.sumOvertimeHours(startDate,
                // endDate, deptId);
                // long actualWorkDays = attendanceRepository.countActualWorkDays(startDate,
                // endDate, deptId);

                // // 3. Tính toán Tỷ lệ chuyên cần (Attendance Rate)
                // // Công thức thực tế: (Tổng lượt đi làm thực tế) / (Tổng nhân viên * Số ngày
                // làm
                // // việc định mức)
                // double attendanceRate = 0.0;
                // int workingDaysInMonth = 26; // Giả định định mức 26 ngày/tháng
                // if (activeEmps > 0) {
                // long potentialWorkDays = activeEmps * workingDaysInMonth;
                // attendanceRate = ((double) actualWorkDays / potentialWorkDays) * 100;
                // }

                // // 4. Trả về Response
                // return EmployeeStatisticsResponse.builder()
                // .totalEmployees((int) activeEmps)
                // .activeEmployees((int) activeEmps)
                // .newHires((int) newHires)
                // .resignations(0) // Cần thêm query vào bảng Employee với status 'RESIGNED' và
                // ngày nghỉ
                // // trong kỳ
                // .attendanceRate(Math.round(attendanceRate * 100.0) / 100.0)
                // .totalOvertimeHours(totalOT != null ? totalOT : BigDecimal.ZERO)
                // .build();
                return null;
        }

}
