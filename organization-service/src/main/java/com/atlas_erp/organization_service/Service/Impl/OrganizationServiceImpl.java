package com.atlas_erp.organization_service.Service.Impl;

import com.atlas_erp.organization_service.Dto.OrganizationItemDTO;
import com.atlas_erp.organization_service.Dto.Request.Organization.*;
import com.atlas_erp.organization_service.Dto.Response.Organization.*;
import com.atlas_erp.organization_service.Entity.OrganizationEntity;
import com.atlas_erp.organization_service.Repository.OrganizationRepository;
import com.atlas_erp.organization_service.Service.OrganizationService;
import com.atlas_erp.organization_service.Cache.serviceCache.OrganizationCacheService;
import com.atlas_erp.organization_service.Util.SecurityUtils;
import lombok.RequiredArgsConstructor;
import com.atlas_erp.organization_service.Event.dtoEvent.OrganizationCreatedEvent;
import com.atlas_erp.organization_service.Event.dtoEvent.OrganizationUpdatedEvent;
import com.atlas_erp.organization_service.Event.serviceEvent.OrganizationEventService;
import org.springframework.transaction.annotation.Transactional;
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import com.atlas_erp.organization_service.Exception.UnauthorizedException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final OrganizationCacheService organizationCacheService;
    private final OrganizationEventService organizationEventService;

    @Override
    public OrganizationListResponse getOrganizations(OrganizationListRequest request) {
        // 1. Chuẩn hóa tham số phân trang
        int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() : 1;
        int size = (request.getPageSize() != null && request.getPageSize() > 0) ? request.getPageSize() : 10;

        // 2. Kiểm tra Cache
        OrganizationListResponse cachedData = organizationCacheService.getOrganizationList(page, size);
        if (cachedData != null) {
            return cachedData;
        }

        // 3. Cache Miss -> Truy vấn Database (PageRequest dùng index 0 nên lấy page -
        // 1)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<OrganizationEntity> organizationPage = organizationRepository.findAll(pageable);

        // 4. Mapping sang DTO
        List<OrganizationItemDTO> dtoList = organizationPage.getContent().stream()
                .map(entity -> {
                    OrganizationItemDTO dto = new OrganizationItemDTO();
                    dto.setId(entity.getId().toString());
                    dto.setCode(entity.getCode());
                    dto.setName(entity.getName());
                    dto.setAddress(entity.getAddress());
                    dto.setStatus(entity.getStatus());
                    return dto;
                })
                .toList();

        OrganizationListResponse response = new OrganizationListResponse();
        response.setList(dtoList);
        response.setTotal(organizationPage.getTotalElements());

        // 5. Lưu vào Cache trước khi trả về
        organizationCacheService.putOrganizationList(page, size, response);

        return response;
    }

    @Override
    @Transactional
    public CreateOrganizationResponse createOrganization(CreateOrganizationRequest request) {
        // 1. Validate dữ liệu
        if (organizationRepository.existsByCode(request.getCode())) {
            throw new IllegalStateException("Organization code already exists: " + request.getCode());
        }

        // 2. Lấy userId từ Access Token (Thông qua SecurityContext)
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        // 3. Khởi tạo Entity
        OrganizationEntity organization = OrganizationEntity.builder()
                .code(request.getCode())
                .name(request.getName())
                .address(request.getAddress())
                // Giả sử Entity của bạn có thêm phone/email hoặc lưu vào metadata
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .createdAt(Instant.now())
                .createdBy(currentUserId)
                .build();

        // 4. Lưu vào Database
        organization = organizationRepository.save(organization);

        // 5. XÓA CACHE: Vì có công ty mới, danh sách phân trang cũ ở Redis đã sai
        organizationCacheService.evictAllListCache();

        // 6. Phát Event (Async)
        OrganizationCreatedEvent event = OrganizationCreatedEvent.builder()
                .organizationId(organization.getId())
                .code(organization.getCode())
                .name(organization.getName())
                .createdBy(currentUserId)
                .createdAt(organization.getCreatedAt())
                .build();

        // Giả sử bạn đã inject OrganizationEventService
        organizationEventService.publishCreatedEvent(event);

        // 7. Trả về Response
        return new CreateOrganizationResponse(organization.getId().toString());
    }

    @Override
    @Transactional // Đảm bảo tính toàn vẹn dữ liệu và bắt buộc khi dùng JPA save/update
    public UpdateOrganizationResponse updateOrganization(UUID id, UpdateOrganizationRequest request) {
        // 1. Kiểm tra sự tồn tại của Organization
        OrganizationEntity organization = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization không tồn tại với ID: " + id));

        // 2. Lấy thông tin User đang thực hiện từ Token (Dùng Utility đã viết)
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("Phiên đăng nhập hết hạn hoặc không hợp lệ"));

        // 3. Cập nhật các trường dữ liệu (Không cho sửa 'code' để giữ tính nhất quán)
        organization.setName(request.getName());
        organization.setAddress(request.getAddress());
        // Giả sử Entity của bạn có thêm phone/email trong metadata hoặc các cột riêng
        organization.setStatus(request.getStatus());

        // 4. Lưu vào Database
        organizationRepository.save(organization);

        // 5. QUẢN LÝ CACHE (Xóa cache cũ để tránh dữ liệu sai lệch)
        // Xóa toàn bộ cache danh sách phân trang vì nội dung item đã thay đổi
        organizationCacheService.evictAllListCache();
        // Xóa cache chi tiết của riêng Organization này (nếu có)
        organizationCacheService.evictDetail(id);

        // 6. PHÁT EVENT (Bắn tin cho Workflow & Audit Service)
        OrganizationUpdatedEvent event = OrganizationUpdatedEvent.builder()
                .organizationId(id)
                .name(organization.getName())
                .status(organization.getStatus())
                .updatedBy(currentUserId)
                .build();
        organizationEventService.publishUpdatedEvent(event);

        // 7. Trả về kết quả thành công
        UpdateOrganizationResponse response = new UpdateOrganizationResponse();
        response.setSuccess(true);
        return response;
    }

    @Override
    public OrganizationDetailResponse getOrganizationDetail(UUID id) {
        // 1. Thử lấy từ Redis trước
        OrganizationDetailResponse cachedResponse = organizationCacheService.getDetail(id);
        if (cachedResponse != null) {
            return cachedResponse;
        }

        // 2. Nếu Cache Miss, truy vấn Database
        OrganizationEntity entity = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Organization không tồn tại với ID: " + id));

        // 3. Mapping Entity sang DTO
        OrganizationDetailResponse response = new OrganizationDetailResponse();
        response.setOrganizationId(entity.getId().toString());
        response.setOrganizationCode(entity.getCode());
        response.setOrganizationName(entity.getName());
        response.setTaxCode(entity.getTaxCode());
        response.setAddress(entity.getAddress());
        response.setStatus(entity.getStatus());

        // Giả sử validFrom là ngày thành lập (establishedDate)
        response.setEstablishedDate(entity.getValidFrom());

        // Chuyển đổi Instant (trong Entity) sang LocalDateTime (trong DTO) để hiển thị
        if (entity.getCreatedAt() != null) {
            response.setCreatedAt(LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneId.systemDefault()));
        }

        // 4. Lưu vào Redis cho lần truy cập sau
        organizationCacheService.putDetail(id, response);

        return response;
    }

}
