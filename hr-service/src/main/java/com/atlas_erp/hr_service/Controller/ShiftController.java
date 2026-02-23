package com.atlas_erp.hr_service.Controller;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.atlas_erp.hr_service.Dto.Response.ApiResponse;
import com.atlas_erp.hr_service.Dto.Response.Shift.ShiftDetailResponse;
import com.atlas_erp.hr_service.Dto.Response.Shift.ShiftResponse;
import com.atlas_erp.hr_service.Service.ShiftService;

import lombok.RequiredArgsConstructor;

import java.util.List;
import java.util.UUID;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@RestController
@RequestMapping("/shifts")
@RequiredArgsConstructor
public class ShiftController {

    private final ShiftService shiftService;

    @GetMapping
    public ResponseEntity<ApiResponse<List<ShiftResponse>>> getAllShifts() {
        List<ShiftResponse> shifts = shiftService.getAllShifts();
        ApiResponse<List<ShiftResponse>> response = ApiResponse.success(shifts);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ApiResponse<ShiftDetailResponse>> getShiftById(@PathVariable UUID id) {
        ShiftDetailResponse shiftDetail = shiftService.getShiftById(id);
        ApiResponse<ShiftDetailResponse> response = ApiResponse.success(shiftDetail);
        return ResponseEntity.ok(response);
    }
}
