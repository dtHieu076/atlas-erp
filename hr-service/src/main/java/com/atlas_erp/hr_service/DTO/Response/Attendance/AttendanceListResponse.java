package com.atlas_erp.hr_service.Dto.Response.Attendance;
import lombok.Data;
import java.util.List;

import com.atlas_erp.hr_service.Dto.AttendanceItemDTO;
@Data
public class AttendanceListResponse {

    private List<AttendanceItemDTO> list;
}

