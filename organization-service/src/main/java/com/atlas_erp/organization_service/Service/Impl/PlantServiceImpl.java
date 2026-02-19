package com.atlas_erp.organization_service.Service.Impl;

import com.atlas_erp.organization_service.Dto.PlantItemDTO;
import com.atlas_erp.organization_service.Dto.Request.Plant.*;
import com.atlas_erp.organization_service.Dto.Response.Plant.*;
import com.atlas_erp.organization_service.Entity.PlantEntity;
import com.atlas_erp.organization_service.Event.dtoEvent.PlantCreatedEvent;
import com.atlas_erp.organization_service.Event.serviceEvent.PlantEventService;
import com.atlas_erp.organization_service.Exception.UnauthorizedException;
import com.atlas_erp.organization_service.Repository.OrganizationRepository;
import com.atlas_erp.organization_service.Repository.PlantRepository;
import com.atlas_erp.organization_service.Service.PlantService;
import com.atlas_erp.organization_service.Util.SecurityUtils;
import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;
import com.atlas_erp.organization_service.Cache.serviceCache.PlantCacheService;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PlantServiceImpl implements PlantService {

    private final OrganizationRepository organizationRepository;
    private final PlantRepository plantRepository;
    private final PlantCacheService plantCacheService;
    private final PlantEventService plantEventService;

    @Override
    public PlantListResponse getPlants(PlantListRequest request) {
        // 1. Chuyển đổi và chuẩn hóa tham số
        UUID orgId = UUID.fromString(request.getOrganizationId());
        int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() : 1;
        int size = (request.getPageSize() != null && request.getPageSize() > 0) ? request.getPageSize() : 10;

        // 2. Kiểm tra Cache trước
        PlantListResponse cached = plantCacheService.getPlantList(orgId, page, size);
        if (cached != null)
            return cached;

        // 3. Validate OrganizationId (Đảm bảo công ty tồn tại)
        if (!organizationRepository.existsById(orgId)) {
            throw new ResourceNotFoundException("Organization không tồn tại");
        }

        // 4. Query Database với phân trang
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("code").ascending());
        Page<PlantEntity> plantPage = plantRepository.findByOrganizationId(orgId, pageable);

        // 5. Mapping sang DTO
        List<PlantItemDTO> dtoList = plantPage.getContent().stream()
                .map(entity -> {
                    PlantItemDTO dto = new PlantItemDTO();
                    dto.setId(entity.getId());
                    dto.setCode(entity.getCode());
                    dto.setName(entity.getName());
                    dto.setOrganizationId(entity.getOrganizationId());
                    dto.setAddress(entity.getAddress()); // Map location sang address
                    // Giả sử status được lấy từ logic nghiệp vụ hoặc entity
                    dto.setStatus("ACTIVE");
                    return dto;
                })
                .toList();

        PlantListResponse response = new PlantListResponse();
        response.setList(dtoList);
        response.setTotal(plantPage.getTotalElements());

        // 6. Lưu vào Cache
        plantCacheService.putPlantList(orgId, page, size, response);

        return response;
    }

    @Override
    @Transactional
    public CreatePlantResponse createPlant(CreatePlantRequest request) {
        UUID orgId = UUID.fromString(request.getOrganizationId());

        // 1. Validate: Organization phải tồn tại
        if (!organizationRepository.existsById(orgId)) {
            throw new ResourceNotFoundException("Organization không tồn tại với ID: " + orgId);
        }

        // 2. Validate: Code phải là duy nhất trong cùng một Organization
        if (plantRepository.existsByCodeAndOrganizationId(request.getCode(), orgId)) {
            throw new IllegalStateException("Mã nhà máy " + request.getCode() + " đã tồn tại trong công ty này.");
        }

        // 3. Lấy UserId từ Token (để lưu vết hoặc gán quyền)
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        // 4. Khởi tạo và Lưu Entity
        PlantEntity plant = new PlantEntity();
        plant.setOrganizationId(orgId);
        plant.setCode(request.getCode());
        plant.setName(request.getName());
        plant.setAddress(request.getAddress());
        // Thiết lập các giá trị mặc định nếu cần
        plant.setValidFrom(LocalDate.now());

        plant = plantRepository.save(plant);

        // 5. XỬ LÝ CACHE: Xóa toàn bộ danh sách nhà máy của Org này vì đã có dữ liệu
        // mới
        plantCacheService.evictPlantListByOrg(orgId);

        // 6. PHÁT EVENT (Kafka)
        PlantCreatedEvent event = PlantCreatedEvent.builder()
                .plantId(plant.getId())
                .organizationId(orgId)
                .code(plant.getCode())
                .name(plant.getName())
                .createdBy(currentUserId)
                .build();
        plantEventService.publishPlantCreatedEvent(event); // Bạn cần thêm hàm này vào EventService

        // 7. Trả về Response
        CreatePlantResponse response = new CreatePlantResponse();
        response.setId(plant.getId().toString());
        return response;
    }
}
