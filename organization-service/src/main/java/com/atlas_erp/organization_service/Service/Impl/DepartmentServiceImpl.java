package com.atlas_erp.organization_service.Service.Impl;

import com.atlas_erp.organization_service.Dto.DepartmentItemDTO;
import com.atlas_erp.organization_service.Dto.Request.Department.*;
import com.atlas_erp.organization_service.Dto.Response.Department.*;
import com.atlas_erp.organization_service.Entity.DepartmentEntity;
import com.atlas_erp.organization_service.Entity.PlantEntity;
import com.atlas_erp.organization_service.Event.dtoEvent.DepartmentCreatedEvent;
import com.atlas_erp.organization_service.Event.serviceEvent.DepartmentEventService;
import com.atlas_erp.organization_service.Exception.UnauthorizedException;
import com.atlas_erp.organization_service.Repository.DepartmentRepository;
import com.atlas_erp.organization_service.Repository.PlantRepository;
import com.atlas_erp.organization_service.Service.DepartmentService;
import lombok.RequiredArgsConstructor;
import com.atlas_erp.organization_service.Cache.serviceCache.DepartmentCacheService;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import com.atlas_erp.organization_service.Util.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class DepartmentServiceImpl implements DepartmentService {

    private final DepartmentRepository departmentRepository;
    private final DepartmentCacheService departmentCacheService;
    private final PlantRepository plantRepository;
    private final DepartmentEventService departmentEventService;

    @Override
    public DepartmentListResponse getDepartments(DepartmentListRequest request) {
        // 1. Chuẩn hóa đầu vào
        UUID plantId = UUID.fromString(request.getPlantId());
        int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() : 1;
        int size = (request.getPageSize() != null && request.getPageSize() > 0) ? request.getPageSize() : 10;

        // 2. Kiểm tra Cache
        DepartmentListResponse cached = departmentCacheService.getDeptList(plantId, page, size);
        if (cached != null)
            return cached;

        // 3. Validate: Kiểm tra nhà máy (Plant) có tồn tại không
        if (!plantRepository.existsById(plantId)) {
            throw new ResourceNotFoundException("Nhà máy (Plant) không tồn tại với ID: " + plantId);
        }

        // 4. Query Database
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("code").ascending());
        Page<DepartmentEntity> deptPage = departmentRepository.findByPlantId(plantId, pageable);

        // 5. Mapping sang DTO
        List<DepartmentItemDTO> dtoList = deptPage.getContent().stream()
                .map(entity -> {
                    DepartmentItemDTO dto = new DepartmentItemDTO();
                    dto.setId(entity.getId().toString());
                    dto.setCode(entity.getCode());
                    dto.setName(entity.getName());
                    dto.setPlantId(entity.getPlantId().toString());
                    // Logic status: Kiểm tra ngày hiệu lực (validTo)
                    String status = (entity.getValidTo() == null || entity.getValidTo().isAfter(LocalDate.now()))
                            ? "ACTIVE"
                            : "INACTIVE";
                    dto.setStatus(status);
                    return dto;
                })
                .collect(Collectors.toList());

        DepartmentListResponse response = new DepartmentListResponse();
        response.setList(dtoList);
        response.setTotal(deptPage.getTotalElements());

        // 6. Lưu Cache
        departmentCacheService.putDeptList(plantId, page, size, response);

        return response;
    }

    @Override
    @Transactional
    public CreateDepartmentResponse createDepartment(CreateDepartmentRequest request) {
        UUID plantId = UUID.fromString(request.getPlantId());

        // 1. Validate: Nhà máy (Plant) phải tồn tại
        if (!plantRepository.existsById(plantId)) {
            throw new ResourceNotFoundException("Nhà máy không tồn tại với ID: " + plantId);
        }

        // 2. Validate: Mã phòng ban phải là duy nhất trong cùng một Nhà máy
        if (departmentRepository.existsByCodeAndPlantId(request.getCode(), plantId)) {
            throw new IllegalStateException("Mã phòng ban '" + request.getCode() + "' đã tồn tại trong nhà máy này.");
        }

        // 3. Lấy thông tin User thực hiện
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        // 4. Khởi tạo và Lưu Entity
        DepartmentEntity department = new DepartmentEntity();
        department.setPlantId(plantId);
        department.setCode(request.getCode());
        department.setName(request.getName());
        department.setValidFrom(LocalDate.now());
        // Mặc định costCenterCode hoặc các trường khác nếu cần

        department = departmentRepository.save(department);

        // 5. XỬ LÝ CACHE: Xóa danh sách phòng ban của Nhà máy này
        departmentCacheService.evictDeptListByPlant(plantId);

        // 6. PHÁT EVENT (Kafka)
        DepartmentCreatedEvent event = DepartmentCreatedEvent.builder()
                .departmentId(department.getId())
                .plantId(plantId)
                .code(department.getCode())
                .name(department.getName())
                .createdBy(currentUserId)
                .build();

        // Giả sử bạn đã tạo DepartmentEventService tương tự PlantEventService
        departmentEventService.publishDepartmentCreatedEvent(event);

        // 7. Trả về Response
        CreateDepartmentResponse response = new CreateDepartmentResponse();
        response.setId(department.getId().toString());
        return response;
    }

    @Override
    public DepartmentDetailResponse getDepartmentDetail(UUID id) {
        // 1. Kiểm tra Cache chi tiết trước
        DepartmentDetailResponse cached = departmentCacheService.getDeptDetail(id);
        if (cached != null)
            return cached;

        // 2. Load Entity từ Database
        DepartmentEntity entity = departmentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Phòng ban không tồn tại với ID: " + id));

        // 3. Mapping dữ liệu
        DepartmentDetailResponse response = new DepartmentDetailResponse();
        response.setDepartmentId(entity.getId().toString());
        response.setDepartmentCode(entity.getCode());
        response.setDepartmentName(entity.getName());

        // Lưu ý: DepartmentEntity của bạn chỉ có plantId.
        // Nếu muốn lấy organizationId, bạn phải thông qua PlantEntity.
        PlantEntity plant = plantRepository.findById(entity.getPlantId())
                .orElseThrow(() -> new ResourceNotFoundException("Lỗi dữ liệu: Nhà máy không tồn tại"));
        response.setOrganizationId(plant.getOrganizationId().toString());

        // managerId hiện tại chưa có trong Entity của bạn, tôi giả định lấy từ metadata
        // hoặc trả về null
        response.setManagerId(null);

        // Logic Status dựa trên ngày hiệu lực
        String status = (entity.getValidTo() == null || entity.getValidTo().isAfter(LocalDate.now()))
                ? "ACTIVE"
                : "INACTIVE";
        response.setStatus(status);

        // Chuyển đổi Instant sang LocalDateTime
        if (entity.getCreatedAt() != null) {
            response.setCreatedAt(LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneId.systemDefault()));
        } else {
            response.setCreatedAt(LocalDateTime.now());
        }

        // 4. Lưu vào Redis (TTL 60 phút)
        departmentCacheService.putDeptDetail(id, response);

        return response;
    }

}
