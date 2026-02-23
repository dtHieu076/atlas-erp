package com.atlas_erp.hr_service.Service;

import java.util.List;
import java.util.UUID;

import com.atlas_erp.hr_service.Dto.Response.Shift.ShiftDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Shift.ShiftResponse;

public interface ShiftService {
    List<ShiftResponse> getAllShifts();

    ShiftDetailResponse getShiftById(UUID id);
}
