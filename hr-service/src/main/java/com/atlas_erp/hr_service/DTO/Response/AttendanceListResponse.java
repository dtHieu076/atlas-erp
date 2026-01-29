package com.atlas_erp.hr_service.DTO.Response;
import com.atlas_erp.hr_service.DTO.AttendanceItemDTO;
import lombok.Data;
import java.util.List;
@Data
public class AttendanceListResponse {

    private List<AttendanceItemDTO> list;
}

