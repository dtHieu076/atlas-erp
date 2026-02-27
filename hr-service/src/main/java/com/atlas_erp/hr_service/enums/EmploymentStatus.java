package com.atlas_erp.hr_service.enums;

/**
 * Trạng thái làm việc của nhân viên trong hệ thống HRM.
 * Phân loại gồm:
 * - Working status
 * - Leave status
 * - Separation status
 */
public enum EmploymentStatus {

    // ===== Working Status =====
    PROBATION, // Thử việc
    ACTIVE, // Đang làm chính thức
    SUSPENDED, // Tạm đình chỉ công tác

    // ===== Leave Status =====
    MATERNITY_LEAVE, // Nghỉ thai sản
    LONG_TERM_LEAVE, // Nghỉ dài hạn (ốm dài ngày, không lương...)
    SABBATICAL, // Nghỉ phép đặc biệt dài hạn

    // ===== Separation Status =====
    RESIGNED, // Nhân viên tự nghỉ
    TERMINATED // Công ty chấm dứt hợp đồng
}