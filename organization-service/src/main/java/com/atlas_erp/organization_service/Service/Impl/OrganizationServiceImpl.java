package com.atlas_erp.organization_service.Service.Impl;

import com.atlas_erp.organization_service.Dto.DepartmentItemDTO;
import com.atlas_erp.organization_service.Dto.OrganizationItemDTO;
import com.atlas_erp.organization_service.Dto.PlantItemDTO;
import com.atlas_erp.organization_service.Dto.Request.Organization.*;
import com.atlas_erp.organization_service.Dto.Response.Organization.*;
import com.atlas_erp.organization_service.Entity.DepartmentEntity;
import com.atlas_erp.organization_service.Entity.OrganizationEntity;
import com.atlas_erp.organization_service.Entity.PlantEntity;
import com.atlas_erp.organization_service.Repository.DepartmentRepository;
import com.atlas_erp.organization_service.Repository.OrganizationRepository;
import com.atlas_erp.organization_service.Repository.PlantRepository;
import com.atlas_erp.organization_service.Service.OrganizationService;
import com.atlas_erp.organization_service.Cache.serviceCache.OrganizationCacheService;
import com.atlas_erp.organization_service.Util.SecurityUtils;
import com.atlas_erp.organization_service.Event.dtoEvent.OrganizationCreatedEvent;
import com.atlas_erp.organization_service.Event.dtoEvent.OrganizationUpdatedEvent;
import com.atlas_erp.organization_service.Event.serviceEvent.OrganizationEventService;
import com.atlas_erp.organization_service.Exception.UnauthorizedException;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.data.domain.*;

import org.apache.kafka.common.errors.ResourceNotFoundException;

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
    private final PlantRepository plantRepository;
    private final DepartmentRepository departmentRepository;

    // =====================================================
    // 1. GET LIST
    // =====================================================
    @Override
    public OrganizationListResponse getOrganizations(OrganizationListRequest request) {

        int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() : 1;
        int size = (request.getPageSize() != null && request.getPageSize() > 0) ? request.getPageSize() : 10;

        // Check cache
        OrganizationListResponse cached = organizationCacheService.getOrganizationList(page, size);
        if (cached != null) {
            return cached;
        }

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        Page<OrganizationEntity> pageData = organizationRepository.findAll(pageable);

        List<OrganizationItemDTO> dtoList = pageData.getContent()
                .stream()
                .map(this::mapToItemDTO)
                .toList();

        OrganizationListResponse response = new OrganizationListResponse();
        response.setList(dtoList);
        response.setTotal(pageData.getTotalElements());

        organizationCacheService.putOrganizationList(page, size, response);

        return response;
    }

    // =====================================================
    // 2. CREATE
    // =====================================================
    @Override
    @Transactional
    public CreateOrganizationResponse createOrganization(CreateOrganizationRequest request) {

        if (organizationRepository.existsByCode(request.getCode())) {
            throw new IllegalStateException("Organization code already exists: " + request.getCode());
        }

        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        OrganizationEntity entity = OrganizationEntity.builder()
                .code(request.getCode())
                .name(request.getName())
                .address(request.getAddress())
                .taxCode(request.getTaxCode())
                .status(request.getStatus() != null ? request.getStatus() : "ACTIVE")
                .createdAt(Instant.now())
                .createdBy(currentUserId)
                .build();

        entity = organizationRepository.save(entity);

        // Clear cache
        organizationCacheService.evictAllListCache();

        // Publish event
        OrganizationCreatedEvent event = OrganizationCreatedEvent.builder()
                .organizationId(entity.getId())
                .code(entity.getCode())
                .name(entity.getName())
                .createdBy(currentUserId)
                .createdAt(entity.getCreatedAt())
                .build();

        organizationEventService.publishCreatedEvent(event);

        return new CreateOrganizationResponse(entity.getId().toString());
    }

    // =====================================================
    // 3. UPDATE
    // =====================================================
    @Override
    @Transactional
    public UpdateOrganizationResponse updateOrganization(UUID id, UpdateOrganizationRequest request) {

        OrganizationEntity entity = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organization not found with ID: " + id));

        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("Invalid authentication token"));

        entity.setName(request.getName());
        entity.setAddress(request.getAddress());
        entity.setTaxCode(request.getTaxCode());
        entity.setStatus(request.getStatus());

        organizationRepository.save(entity);

        // Clear cache
        organizationCacheService.evictAllListCache();
        organizationCacheService.evictDetail(id);

        // Publish event
        OrganizationUpdatedEvent event = OrganizationUpdatedEvent.builder()
                .organizationId(id)
                .name(entity.getName())
                .status(entity.getStatus())
                .updatedBy(currentUserId)
                .build();

        organizationEventService.publishUpdatedEvent(event);

        UpdateOrganizationResponse response = new UpdateOrganizationResponse();
        response.setSuccess(true);
        return response;
    }

    // =====================================================
    // 4. GET DETAIL
    // =====================================================
    @Override
    public OrganizationDetailResponse getOrganizationDetail(UUID id) {

        OrganizationDetailResponse cached = organizationCacheService.getDetail(id);
        if (cached != null) {
            return cached;
        }

        OrganizationEntity entity = organizationRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(
                        "Organization not found with ID: " + id));

        OrganizationDetailResponse response = mapToDetailResponse(entity);

        organizationCacheService.putDetail(id, response);

        return response;
    }

    // =====================================================
    // 5. GET ORGANIZATION TREE
    // =====================================================
    @Override
    public OrganizationTreeResponse getOrganizationTree() {

        // 1. Check Redis trước
        OrganizationTreeResponse cached = organizationCacheService.getTree();
        if (cached != null) {
            return cached;
        }

        // 2. Cache Miss → Query DB
        List<OrganizationEntity> organizations = organizationRepository.findAll();
        List<PlantEntity> plants = plantRepository.findAll();
        List<DepartmentEntity> departments = departmentRepository.findAll();

        OrganizationTreeResponse response = new OrganizationTreeResponse();
        response.setCompanies(
                organizations.stream().map(this::mapToItemDTO).toList());
        response.setBranches(
                plants.stream().map(this::mapToPlantDTO).toList());
        response.setDepartments(
                departments.stream().map(this::mapToDepartmentDTO).toList());

        // 3. Lưu vào Redis
        organizationCacheService.putTree(response);

        return response;
    }

    // =====================================================
    // MAPPING METHODS
    // =====================================================
    private PlantItemDTO mapToPlantDTO(PlantEntity entity) {

        PlantItemDTO dto = new PlantItemDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setOrganizationId(entity.getOrganizationId());
        dto.setAddress(entity.getAddress());
        dto.setStatus(entity.getStatus());

        return dto;
    }

    private DepartmentItemDTO mapToDepartmentDTO(DepartmentEntity entity) {

        DepartmentItemDTO dto = new DepartmentItemDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setPlantId(entity.getPlantId());
        dto.setStatus(entity.getStatus());

        return dto;
    }

    private OrganizationItemDTO mapToItemDTO(OrganizationEntity entity) {

        OrganizationItemDTO dto = new OrganizationItemDTO();
        dto.setId(entity.getId());
        dto.setCode(entity.getCode());
        dto.setName(entity.getName());
        dto.setAddress(entity.getAddress());
        dto.setStatus(entity.getStatus());

        return dto;
    }

    private OrganizationDetailResponse mapToDetailResponse(OrganizationEntity entity) {

        OrganizationDetailResponse response = new OrganizationDetailResponse();

        response.setOrganizationId(entity.getId().toString());
        response.setOrganizationCode(entity.getCode());
        response.setOrganizationName(entity.getName());
        response.setTaxCode(entity.getTaxCode());
        response.setAddress(entity.getAddress());
        response.setStatus(entity.getStatus());
        response.setEstablishedDate(entity.getValidFrom());

        if (entity.getCreatedAt() != null) {
            response.setCreatedAt(
                    LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneId.systemDefault()));
        }

        return response;
    }
}
