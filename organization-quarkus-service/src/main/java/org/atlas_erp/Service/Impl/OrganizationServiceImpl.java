package org.atlas_erp.Service.Impl;

import org.atlas_erp.DTO.DepartmentItemDTO;
import org.atlas_erp.DTO.OrganizationItemDTO;
import org.atlas_erp.DTO.PlantItemDTO;
import org.atlas_erp.DTO.OrganizationTreeResponse;
import org.atlas_erp.Entity.DepartmentEntity;
import org.atlas_erp.Entity.OrganizationEntity;
import org.atlas_erp.Entity.PlantEntity;
import org.atlas_erp.Repository.DepartmentRepository;
import org.atlas_erp.Repository.OrganizationRepository;
import org.atlas_erp.Repository.PlantRepository;
import org.atlas_erp.Service.OrganizationService;

import jakarta.enterprise.context.ApplicationScoped;
import lombok.RequiredArgsConstructor;
import java.util.List;

@ApplicationScoped // Thay thế @Service
@RequiredArgsConstructor
public class OrganizationServiceImpl implements OrganizationService {

    private final OrganizationRepository organizationRepository;
    private final PlantRepository plantRepository;
    private final DepartmentRepository departmentRepository;

    // TODO: Bỏ comment khi bạn tích hợp Redis vào Quarkus
    // private final OrganizationCacheService organizationCacheService;

    @Override
    public OrganizationTreeResponse getOrganizationTree() {

        // 1. TODO: Check Redis trước
        // OrganizationTreeResponse cached = organizationCacheService.getTree();
        // if (cached != null) return cached;

        // 2. Cache Miss → Query DB (Dùng Panache .listAll() thay cho .findAll())
        List<OrganizationEntity> organizations = organizationRepository.listAll();
        List<PlantEntity> plants = plantRepository.listAll();
        List<DepartmentEntity> departments = departmentRepository.listAll();

        OrganizationTreeResponse response = new OrganizationTreeResponse();
        response.setCompanies(organizations.stream().map(this::mapToItemDTO).toList());
        response.setBranches(plants.stream().map(this::mapToPlantDTO).toList());
        response.setDepartments(departments.stream().map(this::mapToDepartmentDTO).toList());

        // 3. TODO: Lưu vào Redis
        // organizationCacheService.putTree(response);

        return response;
    }

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
}
