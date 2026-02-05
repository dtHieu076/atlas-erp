package com.atlas_erp.organization_service.Service.Impl;

// 1. Java Standard Libraries
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

// 2. Third-party Libraries (Spring, Lombok, Apache)
import org.apache.kafka.common.errors.ResourceNotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

// 3. Project - DTOs & Request/Response
import com.atlas_erp.organization_service.Dto.TeamItemDTO;
import com.atlas_erp.organization_service.Dto.Request.Team.CreateTeamRequest;
import com.atlas_erp.organization_service.Dto.Request.Team.TeamListRequest;
import com.atlas_erp.organization_service.Dto.Response.Team.CreateTeamResponse;
import com.atlas_erp.organization_service.Dto.Response.Team.TeamDetailResponse;
import com.atlas_erp.organization_service.Dto.Response.Team.TeamListResponse;

// 4. Project - Internal Services & Entities
import com.atlas_erp.organization_service.Cache.serviceCache.TeamCacheService;
import com.atlas_erp.organization_service.Entity.TeamEntity;
import com.atlas_erp.organization_service.Event.dtoEvent.TeamCreatedEvent;
import com.atlas_erp.organization_service.Event.serviceEvent.TeamEventService;
import com.atlas_erp.organization_service.Exception.UnauthorizedException;
import com.atlas_erp.organization_service.Repository.DepartmentRepository;
import com.atlas_erp.organization_service.Repository.TeamRepository;
import com.atlas_erp.organization_service.Service.TeamService;
import com.atlas_erp.organization_service.Util.SecurityUtils;

@Service
@RequiredArgsConstructor
public class TeamServiceImpl implements TeamService {

    private final TeamRepository teamRepository;
    private final TeamCacheService teamCacheService;
    private final DepartmentRepository departmentRepository;
    private final TeamEventService teamEventService;

    @Override
    public TeamListResponse getTeams(TeamListRequest request) {
        // 1. Chuyển đổi và chuẩn hóa tham số
        UUID deptId = UUID.fromString(request.getDepartmentId());
        int page = (request.getPage() != null && request.getPage() > 0) ? request.getPage() : 1;
        int size = (request.getPageSize() != null && request.getPageSize() > 0) ? request.getPageSize() : 10;

        // 2. Kiểm tra Cache Redis
        TeamListResponse cached = teamCacheService.getTeamList(deptId, page, size);
        if (cached != null)
            return cached;

        // 3. Validate: Đảm bảo phòng ban tồn tại
        if (!departmentRepository.existsById(deptId)) {
            throw new ResourceNotFoundException("Phòng ban không tồn tại với ID: " + deptId);
        }

        // 4. Truy vấn DB với Phân trang & Sắp xếp theo mã (code)
        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("code").ascending());
        Page<TeamEntity> teamPage = teamRepository.findByDepartmentId(deptId, pageable);

        // 5. Mapping sang DTO
        List<TeamItemDTO> dtoList = teamPage.getContent().stream()
                .map(entity -> {
                    TeamItemDTO dto = new TeamItemDTO();
                    dto.setId(entity.getId().toString());
                    dto.setCode(entity.getCode());
                    dto.setName(entity.getName());
                    dto.setDepartmentId(entity.getDepartmentId().toString());
                    dto.setLeaderId(null); // Trường này thường lấy từ HR Service hoặc cột leader_id
                    dto.setStatus("ACTIVE"); // Mặc định ACTIVE cho Team
                    return dto;
                })
                .collect(Collectors.toList());

        TeamListResponse response = new TeamListResponse();
        response.setList(dtoList);
        response.setTotal(teamPage.getTotalElements());

        // 6. Lưu vào Cache trước khi trả về
        teamCacheService.putTeamList(deptId, page, size, response);

        return response;
    }

    @Override
    @Transactional
    public CreateTeamResponse createTeam(CreateTeamRequest request) {
        // 1. Lấy userId từ Access Token (Thông qua SecurityContext)
        // Đây chính là ID người thực hiện, chúng ta dùng nó làm Leader hoặc người tạo
        UUID currentUserId = SecurityUtils.getCurrentUserId()
                .orElseThrow(() -> new UnauthorizedException("User not authenticated"));

        UUID deptId = UUID.fromString(request.getDepartmentId());

        // 2. Validate: Phòng ban phải tồn tại
        if (!departmentRepository.existsById(deptId)) {
            throw new ResourceNotFoundException("Phòng ban không tồn tại với ID: " + deptId);
        }

        // 3. Validate: Mã tổ (code) duy nhất trong phòng ban
        if (teamRepository.existsByCodeAndDepartmentId(request.getCode(), deptId)) {
            throw new IllegalStateException("Mã tổ '" + request.getCode() + "' đã tồn tại trong phòng ban này.");
        }

        // 4. Khởi tạo Entity và gán ID từ Token
        TeamEntity team = new TeamEntity();
        team.setDepartmentId(deptId);
        team.setCode(request.getCode());
        team.setName(request.getName());

        // Nếu bạn muốn lưu người thực hiện vào cột leaderId hoặc createdBy trong DB
        // team.setLeaderId(currentUserId);

        team = teamRepository.save(team);

        // 5. Xử lý Cache: Xóa danh sách tổ của phòng ban này để cập nhật bản mới
        teamCacheService.evictTeamListByDept(deptId);

        // 6. Phát Event (Kafka) - Vẫn gửi kèm currentUserId để các service khác biết ai
        // đã tạo
        TeamCreatedEvent event = TeamCreatedEvent.builder()
                .teamId(team.getId())
                .departmentId(deptId)
                .leaderId(currentUserId) // Lấy luôn ID từ token làm leader trong event
                .createdBy(currentUserId)
                .build();

        teamEventService.publishTeamCreatedEvent(event);

        // 7. Trả về Response
        CreateTeamResponse response = new CreateTeamResponse();
        response.setId(team.getId().toString());
        return response;
    }

    @Override
    public TeamDetailResponse getTeamDetail(UUID id) {
        // 1. Kiểm tra Cache (Dùng đúng biến id)
        TeamDetailResponse cached = teamCacheService.getTeamDetail(id);
        if (cached != null)
            return cached;

        // 2. Truy vấn DB
        TeamEntity entity = teamRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Tổ không tồn tại với ID: " + id));

        // 3. Mapping
        TeamDetailResponse response = new TeamDetailResponse();
        response.setTeamId(entity.getId().toString());
        response.setTeamCode(entity.getCode());
        response.setTeamName(entity.getName());
        response.setDepartmentId(entity.getDepartmentId().toString());

        // Trả về leaderId từ DB (không cần gọi HR service để tối ưu tốc độ)
        response.setLeaderId(entity.getLeaderId() != null ? entity.getLeaderId().toString() : null);

        response.setStatus("ACTIVE");

        // Map createdAt từ Instant sang LocalDateTime
        response.setCreatedAt(entity.getCreatedAt() != null
                ? LocalDateTime.ofInstant(entity.getCreatedAt(), ZoneId.systemDefault())
                : LocalDateTime.now());

        // 4. Lưu Cache
        teamCacheService.putTeamDetail(id, response);

        return response;
    }

}
