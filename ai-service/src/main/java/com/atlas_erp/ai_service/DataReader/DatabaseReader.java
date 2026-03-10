package com.atlas_erp.ai_service.DataReader;

/**
 * Interface for database reading operations.
 * Provides abstraction for data retrieval from HR database.
 */
public interface DatabaseReader {

    /**
     * Get employee count by status
     */
    long getEmployeeCountByStatus(String status);

    /**
     * Get total active employee count
     */
    long getActiveEmployeeCount();

    /**
     * Get attendance count for a specific date and status
     */
    long getAttendanceCountByDateAndStatus(String date, String status);

    /**
     * Get today's attendance summary
     */
    String getTodayAttendanceSummary();
}
